package org.origin.centres.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.origin.centres.interfaces.IPreHandle;
import org.origin.centres.utils.UserUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zhangjie
 * @version 2018-04-26
 * @apiNote 基础数据模型
 */
@SuppressWarnings(value = {"rawtypes", "unused"})
public abstract class BasicModel implements IPreHandle {

    @TableField(fill = FieldFill.INSERT)
    private String createBy;    // 更新人ID

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;    // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;    // 更新人ID

    @Version()
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;    // 更新时间

    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;     // 逻辑删除标记（0.正常--默认；1.删除）

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String remark;      // 备注

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public void preInsert() {
        // 插入执行
        this.delFlag = 0;
        this.createTime = new Date();
        this.updateTime = new Date();
        this.createBy = UserUtil.getUserId();
        this.updateBy = this.createBy;
    }

    @Override
    public void preUpdate() {
        // 更新执行
        this.updateTime = new Date();
        this.updateBy = UserUtil.getUserId();
    }
}
