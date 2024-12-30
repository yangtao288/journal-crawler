package com.etl.sample;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelAuthorParserApp extends JFrame {
    private JTextArea outputArea;
    private JButton chooseFileButton;
    private static final Pattern AFFILIATION_PATTERN =
            Pattern.compile("(?:(\\d+)\\.|^)\\s*([^,]+),\\s*([^\\s]+)\\s+(\\d{6})");
    private static final Pattern AUTHOR_PATTERN =
            Pattern.compile("(.*?)(\\d+(?:,\\d+)*)?$");

    public ExcelAuthorParserApp() {
        setTitle("Excel Author Parser");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 获取屏幕尺寸
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.width * 0.8);
        int height = (int)(screenSize.height * 0.8);

        // 设置窗口大小
        setPreferredSize(new Dimension(width, height));

        initComponents();
        pack();
        setLocationRelativeTo(null); // 窗口居中
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // 创建按钮
        chooseFileButton = new JButton("选择Excel文件");
        chooseFileButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        chooseFileButton.addActionListener(e -> chooseFile());
        buttonPanel.add(chooseFileButton);

        // 创建输出区域，使用更大的字体
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 添加组件
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // 设置边距
        ((JPanel)getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Excel文件 (*.xlsx, *.xls)", "xlsx", "xls"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            processExcelFile(file);
        }
    }

    private void processExcelFile(File file) {
        outputArea.setText("开始处理文件: " + file.getName() + "\n\n");

        try {
            List<ParsedResult> results = parseExcel(file);
            displayResults(results);
        } catch (IOException e) {
            outputArea.append("错误: " + e.getMessage() + "\n");
        }
    }

    private ParsedResult parseRow(String authorStr, String deptStr) {
        List<Author> authors = new ArrayList<>();
        List<Affiliation> affiliations = new ArrayList<>();
        Map<String, Affiliation> affiliationMap = new HashMap<>();

        // 清理输入数据
        authorStr = authorStr.replaceAll("[（）]", "").trim();
        deptStr = deptStr.replaceAll("[（）]", "").trim();

        // 首先解析机构信息
        String[] deptParts = deptStr.split(";\\s*");
        boolean hasAffiliationNumbers = deptStr.matches(".*\\d+\\..*");

        // 处理机构信息
        for (String deptPart : deptParts) {
            Matcher matcher = AFFILIATION_PATTERN.matcher(deptPart.trim());
            if (matcher.find()) {
                String id = hasAffiliationNumbers ? matcher.group(1) : "1";
                String deptName = matcher.group(2).trim();
                String city = matcher.group(3).trim();
                String postalCode = matcher.group(4).trim();

                Affiliation affiliation = new Affiliation(deptName, city, postalCode);
                affiliationMap.put(id, affiliation);
                affiliations.add(affiliation);
            }
        }

        // 使用Pattern来匹配作者名和机构编号
        Pattern authorPattern = Pattern.compile("([^\\d,]+)(\\d+(?:,\\d+)*)?(?:,|$)");
        Matcher authorMatcher = authorPattern.matcher(authorStr);

        while (authorMatcher.find()) {
            String name = authorMatcher.group(1).trim();
            String affiliationIds = authorMatcher.group(2);

            // 跳过空名称
            if (name.isEmpty()) {
                continue;
            }

            if (affiliationIds == null || affiliationIds.isEmpty()) {
                // 如果没有机构编号，且只有一个机构，使用该机构
                if (affiliationMap.size() == 1) {
                    Affiliation affiliation = affiliationMap.values().iterator().next();
                    authors.add(new Author(name, affiliation.getDeptName()));
                }
            } else {
                // 处理机构编号，支持形如 "1,2" 的格式
                String[] ids = affiliationIds.split(",");
                for (String id : ids) {
                    Affiliation affiliation = affiliationMap.get(id.trim());
                    if (affiliation != null) {
                        authors.add(new Author(name, affiliation.getDeptName()));
                    }
                }
            }
        }

        return new ParsedResult(authors, affiliations);
    }

    private List<ParsedResult> parseExcel(File file) throws IOException {
        List<ParsedResult> results = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = file.getName().endsWith(".xlsx") ?
                     new XSSFWorkbook(fis) : new HSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // 跳过表头
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            // 处理每一行数据
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell authorCell = row.getCell(6);
                Cell deptCell = row.getCell(7);

                if (authorCell != null && deptCell != null) {
                    String authors = getCellValue(authorCell);
                    String depts = getCellValue(deptCell);

                    if (authors != null && !authors.trim().isEmpty() &&
                            depts != null && !depts.trim().isEmpty()) {
                        results.add(parseRow(authors, depts));
                    }
                }
            }
        }

        return results;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return null;
        }
    }

    private void displayResults(List<ParsedResult> results) {
        StringBuilder sb = new StringBuilder();
        int rowNumber = 1;

        for (ParsedResult result : results) {
            sb.append("行 ").append(rowNumber++).append(":\n");
            sb.append("作者信息:\n");
            for (Author author : result.authors) {
                sb.append("  ").append(author).append("\n");
            }
            sb.append("机构信息:\n");
            for (Affiliation affiliation : result.affiliations) {
                sb.append("  ").append(affiliation).append("\n");
            }
            sb.append("\n");
        }

        outputArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        try {
            // 设置界面外观为系统外观
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 启动应用
        SwingUtilities.invokeLater(() -> {
            new ExcelAuthorParserApp().setVisible(true);
        });
    }
}
