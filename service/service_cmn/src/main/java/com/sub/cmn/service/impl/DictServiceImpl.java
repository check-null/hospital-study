package com.sub.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sub.cmn.listener.DictListener;
import com.sub.cmn.mapper.DictMapper;
import com.sub.cmn.service.DictService;
import com.sub.model.cmn.Dict;
import com.sub.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    public List<Dict> findChild(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);

        List<Dict> list = baseMapper.selectList(wrapper);
        list.forEach(dict -> {
            boolean hasChildren = hasChildren(dict.getId());
            dict.setHasChildren(hasChildren);
        });
        return list;
    }

    @Override
    public void exportDict(HttpServletResponse response) {
        // 设置下载信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String filename = "dict";
        response.setHeader("content-disposition", "attachment;filename=" + filename + ".xlsx");

        List<Dict> list = baseMapper.selectList(null);
        List<DictEeVo> voList = list.stream().map(dict -> {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict, dictEeVo);
            return dictEeVo;
        }).collect(Collectors.toList());

        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class)
                    .sheet("dict")
                    .doWrite(voList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void importDict(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), DictEeVo.class, new DictListener(baseMapper))
                    .sheet()
                    .doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDictName(String dictCode, String value) {
        System.out.println("dictCode = " + dictCode);
        System.out.println("value = " + value);
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        // 判断是否有dictCode
        if (StringUtils.isEmpty(dictCode)) {
            wrapper.eq("value", value);
            return baseMapper.selectOne(wrapper).getName();
        } else {
            wrapper.eq("dict_code", dictCode);
            Dict dict = baseMapper.selectOne(wrapper);
            // 根据parent_id 和 value 查询
            QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("parent_id", dict.getId());
            queryWrapper.eq("value", value);
            return baseMapper.selectOne(queryWrapper).getName();
        }
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code", dictCode);
        Dict dict = baseMapper.selectOne(wrapper);
        return findChild(dict.getId());
    }

    private boolean hasChildren(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id);
        return baseMapper.selectCount(wrapper) > 0;
    }
}
