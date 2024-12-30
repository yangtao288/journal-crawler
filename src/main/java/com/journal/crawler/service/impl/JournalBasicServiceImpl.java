package com.journal.crawler.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.journal.crawler.entity.JournalBasic;
import com.journal.crawler.mapper.JournalBasicMapper;
import com.journal.crawler.service.JournalBasicService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class JournalBasicServiceImpl extends
        ServiceImpl<JournalBasicMapper, JournalBasic> implements JournalBasicService {
    @Override
    public JournalBasic findByBasicId(Long basicId) {
        LambdaQueryWrapper<JournalBasic> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(JournalBasic::getId, basicId);
        List<JournalBasic> basicList = list(wrapper);
        if (CollectionUtils.isEmpty(basicList)) {
            return null;
        }
        return basicList.get(0);
    }
}
