package cn.palmap.jilinscience.model;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by 王天明 on 2017/5/8.
 */

@Data
public class User implements Serializable{
    private int id;
    private String loginName;
    private String userName;
    private int sex;
    private long birthday;
    private int type;
    private String headPath;
    private long lastTime;
}
