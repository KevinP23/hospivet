package com.hospivet.veterinario.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospivet.veterinario.model.Veterinario;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface VeterinarioMapper extends BaseMapper<Veterinario> {

    @Select("SELECT id_veterinario FROM veterinario WHERE id_veterinario = #{idVeterinario} FOR UPDATE")
    Long bloquearPorId(@Param("idVeterinario") Long idVeterinario);
}
