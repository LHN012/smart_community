package com.example.smart_community.controller;

import com.example.smart_community.common.Result;
import com.example.smart_community.entity.UserHouses;
import com.example.smart_community.service.UserHousesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-houses")
public class UserHousesController {

    @Autowired
    private UserHousesService userHousesService;

    /**
     * 获取所有用户房屋关系列表
     */
    @GetMapping("/list")
    public Result<List<UserHouses>> list() {
        List<UserHouses> list = userHousesService.listWithUserAndHouseInfo();
        return Result.success(list);
    }

    /**
     * 更新用户房屋关系
     */
    @PutMapping("/update/{id}")
    public Result<String> update(@PathVariable Integer id, @RequestBody UserHouses userHouses) {
        userHouses.setRelationId(id);
        boolean success = userHousesService.updateById(userHouses);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    /**
     * 删除用户房屋关系
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        boolean success = userHousesService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
} 