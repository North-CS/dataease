package io.dataease.map.dao.auto.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * <p>
 * 自定义地理区域分区详情
 * </p>
 *
 * @author fit2cloud
 * @since 2024-11-22
 */
@TableName("core_custom_geo_sub_area")
public class CoreCustomGeoSubArea implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 区域范围
     */
    private String scope;

    /**
     * 自定义地理区域id
     */
    private String geoAreaId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getGeoAreaId() {
        return geoAreaId;
    }

    public void setGeoAreaId(String geoAreaId) {
        this.geoAreaId = geoAreaId;
    }

    @Override
    public String toString() {
        return "CoreCustomGeoSubArea{" +
        "id = " + id +
        ", name = " + name +
        ", scope = " + scope +
        ", geoAreaId = " + geoAreaId +
        "}";
    }
}
