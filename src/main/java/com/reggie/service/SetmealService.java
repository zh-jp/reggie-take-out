package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.SetmealDto;
import com.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    // 获取setmeal表和setmeal_dish表的数据
    public SetmealDto getByIdWithDish(Long id);

    // 更新setmeal表和setmeal_dish表
    public void updateWithDish(SetmealDto setmealDto);

    // 增加新数据在setmeal和setmeal_dish表
    public void saveWithDish(SetmealDto setmealDto);

    // 逻辑删除setmeal和setmeal_dish表中的数据
    public void removeWithDish(List<Long> ids);
}
