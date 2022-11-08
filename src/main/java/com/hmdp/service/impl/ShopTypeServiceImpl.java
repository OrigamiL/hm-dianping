package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.hmdp.utils.RedisConstants.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {
        //1.从redis中查询list缓存
        String typeList = stringRedisTemplate.opsForValue().get(SHOP_LIST);
        if (StrUtil.isNotBlank(typeList)) {
            //2.如果有，则返回
            List<ShopType> list = JSONUtil.toList(typeList, ShopType.class);
            return Result.ok(list);
        }
        //3.如果没有，从数据库中查询
        List<ShopType> list = query().orderByAsc("sort").list();
        //4.如果没有则返回空
        if (list.isEmpty()) {
            return Result.fail("列表为空！");
        }
        //5.如果有，写入redis
        stringRedisTemplate.opsForValue().set(SHOP_LIST, JSONUtil.toJsonStr(list));
        //6.返回
        return Result.ok(list);
    }
}
