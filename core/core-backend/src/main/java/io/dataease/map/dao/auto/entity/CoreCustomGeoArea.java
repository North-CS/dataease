package io.dataease.map.dao.auto.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * <p>
 * 自定义地理区域
 * </p>
 *
 * @author fit2cloud
 * @since 2024-11-22
 */
@TableName("core_custom_geo_area")
public class CoreCustomGeoArea implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;

    /**
     * 区域名称
     */
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CoreCustomGeoArea{" +
        "id = " + id +
        ", name = " + name +
        "}";
    }
}
