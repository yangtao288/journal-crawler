package com.journal.crawler.knowledgegraph.util;

import com.google.common.collect.Lists;
import com.journal.crawler.knowledgegraph.model.Edge;
import com.journal.crawler.knowledgegraph.model.Node;
import com.journal.crawler.knowledgegraph.model.Person;
import com.journal.crawler.knowledgegraph.model.ProjectDoc;

import java.util.*;

public class RelationshipParser {
    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private int nodeIdCounter = 1;
    private Map<String, Integer> nameToIdMap = new HashMap<>();

    public void parseRelationships(ProjectDoc project) {
        // 创建和该文档负责人有关联关系的节点
        Set<Person> allPersons = new HashSet<>();
        allPersons.add(project.getLeader());

        // 解析关系人
        parseInterPersonalRelationships(project);
    }

    private void parseInterPersonalRelationships(ProjectDoc project) {
        Person personLeader = project.getLeader();
        addPersonNode(personLeader);
        addProjectNode(project);
        relationshipWithOwnProject(personLeader, project);
        addOrganizationNode(personLeader.getOrganization());
        relationshipWithSchool(personLeader, personLeader.getOrganization());

        List<ProjectDoc> projectDocs = project.getSimilarProject();
        for (ProjectDoc projectDoc : projectDocs) {
            List<Person> persons = Lists.newArrayList();
            Person projectLeader = projectDoc.getLeader();
            persons.add(projectLeader);
            persons.addAll(projectDoc.getMembers());

            boolean isExists = false;
            for (Person person : persons) {
                // 三元素相同，认定是同一个人
                if (person.getName().equals(personLeader.getName()) &&
                        person.getOrganization().equals(personLeader.getOrganization()) &&
                        person.getTitle().equals(personLeader.getTitle())) {
                    isExists = true;
                }
            }

            if (isExists) {
                // 设置人员组
                addPersonNode(projectLeader);
                addProjectNode(projectDoc);
                relationshipWithOwnProject(projectLeader, projectDoc);
                addOrganizationNode(projectLeader.getOrganization());
                relationshipWithSchool(projectLeader, projectLeader.getOrganization());

                // 建立关系边设置
                relationshipWithPerson(personLeader, projectLeader);

//                // 组员列表
//                for (Person person : projectDoc.getMembers()) {
//                    // 剔除目标文档项目负责人在该项目存在组员情形
//                    if (!(person.getName().equals(personLeader.getName()) &&
//                            person.getTitle().equals(personLeader.getTitle()) &&
//                            person.getOrganization().equals(personLeader.getOrganization()))) {
//                        // 设置人员组
//                        addPersonNode(person);
//                        relationshipWithProject(person, projectDoc);
//                        addOrganizationNode(person.getOrganization());
//                        relationshipWithSchool(person, person.getOrganization());
//
//                        // 建立关系边设置
//                        relationshipWithPerson(personLeader, person);
//                    }
//                }
            }
        }
    }

    private void addPersonNode(Person person) {
        if (!nameToIdMap.containsKey(person.getName())) {
            Integer id = nodeIdCounter++;
            nameToIdMap.put(person.getName(), id);
            nodes.add(new Node(id, person.getName(), "person"));
        }
    }

    private void addProjectNode(ProjectDoc projectDoc) {
        if (!nameToIdMap.containsKey(projectDoc.getProjectName())) {
            Integer id = nodeIdCounter++;
            nameToIdMap.put(projectDoc.getProjectName(), id);
            nodes.add(new Node(id, projectDoc.getProjectName(), "project"));
        }
    }

    private void addOrganizationNode(String organization) {
        if (!nameToIdMap.containsKey(organization)) {
            Integer id = nodeIdCounter ++;
            nameToIdMap.put(organization, id);
            nodes.add(new Node(id, organization, "school"));
        }
    }

    private void relationshipWithSchool(Person leader, String organization) {
        Integer personId = nameToIdMap.get(leader.getName());
        Integer organizationId = nameToIdMap.get(organization);
        edges.add(new Edge(personId, organizationId, "单位"));
    }

    private void relationshipWithOwnProject(Person leader, ProjectDoc projectDoc) {
        Integer personId = nameToIdMap.get(leader.getName());
        Integer projectId = nameToIdMap.get(projectDoc.getProjectName());
        edges.add(new Edge(personId, projectId, "负责"));
    }

    private void relationshipWithProject(Person leader, ProjectDoc projectDoc) {
        Integer personId = nameToIdMap.get(leader.getName());
        Integer projectId = nameToIdMap.get(projectDoc.getProjectName());
        edges.add(new Edge(personId, projectId, "参与"));
    }

    private void relationshipWithPerson(Person leader, Person person) {
        // 解析师生关系
        if (isTeacherStudentRelationship(leader, person)) {
            Integer teacherId = nameToIdMap.get(getTeacher(leader, person).getName());
            Integer studentId = nameToIdMap.get(getStudent(leader, person).getName());
            edges.add(new Edge(teacherId, studentId, "师生"));
        }

        // 解析合作关系
        if (isCollaborationRelationship(leader, person)) {
            Integer id1 = nameToIdMap.get(leader.getName());
            Integer id2 = nameToIdMap.get(person.getName());
            edges.add(new Edge(id1, id2, "合作"));
        }

        // 解析同事关系
        if (isColleagueRelationship(leader, person)) {
            Integer id1 = nameToIdMap.get(leader.getName());
            Integer id2 = nameToIdMap.get(person.getName());
            edges.add(new Edge(id1, id2, "同事"));
        }

        // 解析同学关系
        if (isClassmateRelationship(leader, person)) {
            Integer id1 = nameToIdMap.get(leader.getName());
            Integer id2 = nameToIdMap.get(person.getName());
            edges.add(new Edge(id1, id2, "同学"));
        }
    }

    private boolean isTeacherStudentRelationship(Person person1, Person person2) {
        if (person1.getOrganization().equals(person2.getOrganization())) {
            return (isTeacher(person1) && isStudent(person2)) ||
                    (isTeacher(person2) && isStudent(person1));
        }
        return false;
    }

    private boolean isCollaborationRelationship(Person person1, Person person2) {
        return !person1.getOrganization().equals(person2.getOrganization()) &&
                !isStudent(person1) && !isStudent(person2);
    }

    private boolean isColleagueRelationship(Person person1, Person person2) {
        return person1.getOrganization().equals(person2.getOrganization()) &&
                !isStudent(person1) && !isStudent(person2);
    }

    private boolean isClassmateRelationship(Person person1, Person person2) {
        return person1.getOrganization().equals(person2.getOrganization()) &&
                isStudent(person1) && isStudent(person2);
    }

    private boolean isTeacher(Person person) {
        return Arrays.asList("讲师", "教授", "副教授", "研究员", "副研究员").contains(person.getTitle());
    }

    private boolean isStudent(Person person) {
        return Arrays.asList("学生", "研究生", "博士").contains(person.getTitle());
    }

    private Person getTeacher(Person person1, Person person2) {
        return isTeacher(person1) ? person1 : person2;
    }

    private Person getStudent(Person person1, Person person2) {
        return isStudent(person1) ? person1 : person2;
    }

    private ProjectDoc findProjectById(List<ProjectDoc> projects, String projectId) {
        return projects.stream()
                .filter(p -> p.getProjectId().equals(projectId))
                .findFirst()
                .orElse(null);
    }

    // Getter methods for nodes and edges
    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}