package com.product.sampling.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskEntity implements Serializable {
    public String id;//任务唯一id
    public String planid;//所属计划id
    public String companyid;//抽样企业id
    public String remark;//任务备注
    public String starttime;//任务开始时间
    public String endtime;//任务结束时间

    public String taskstatus;//任务状态 0待办1本地保存2上传3退回修改4已确认5停用
    public String doman;//抽样人
    public String samplingcodes;//抽样单编号

    public String tasktypecount;//抽样产品名
    public String taskCode;//任务编号
    public String planname;//所属计划名
    public String companyname;//企业名

    public String goodscount;//计划抽样批次总数
    public String companyaddress;//企业地址
    public String taskisok;//任务异常状态 0抽样正常1企业拒检2未检测到样品
    public String companyperson;//企业联系人
    public String companytel;//企业联系号码

    public String businesslicence;//企业营业执照
    public String planno;//计划备案号
    public String planfrom;//计划来源
    public String groupPersonCount;//任务每组人数

    public String areasheng;//企业所在省
    public String areashi;//企业所在市
    public String areaqu;//企业所在区/县
    public String taskAddress;//任务提交时的地址
    public String taskTime;//任务提交时间

    public double longitude;//任务提交时的经度
    public double latitude;//任务提交时的维度
    public int leftday;//任务剩余天数


    public List<Pics> pics = new ArrayList<Pics>();
    public List<Videos> voides = new ArrayList<Videos>();
    public boolean isNewRecord;

    public boolean isLoadLocalData;
    public boolean isEditedTaskScene;//是否编辑过现场

    public List<TaskSample> taskSamples = new ArrayList<TaskSample>();

    public HashMap<String, String> unfindSampleInfoMap;//未检测到样品单 填写内容
    public String unfindfile = "";//未检测到样品单 pdf
    public String unfindpicfile = "";//未检测到样品单 照片

    public HashMap<String, String> refuseInfoMap;//企业拒检单 填写内容
    public List<Map<String,Object>> annexfiles = new ArrayList<>();
    public String refusefile = "";//企业拒检单 pdf
    public String refusepicfile = "";//企业拒检单 照片

    public Refuse refuse;//企业拒检单 对象
    public Unfind unfind;//未找到样品单

    public String feedfile = "";
    public String feedpicfile = "";
    public HashMap<String, String> feedInfoMap;

    public String disposalfile = "";//处置单
    public String disposalpicfile = "";
    public HashMap<String, String> adviceInfoMap;

    public Feed feed = new Feed();

    public String plantype;//1生产领域不可以修改 2流通领域可以修改
    public Advice advice = new Advice();

    public double companylongitude;
    public double companylatitude;
    public String suredotime;//开始执行时间:判断是否需要跳出是否确认开始执行任务 null需要不为空不需要

    public TaskEntity() {

    }



    public boolean isUploadedTask() {
        return "2".equals(taskstatus);//是否是已上传状态
    }

    public boolean isCirculationDomain() {
        return "2".equals(plantype);//是否是流通领域
    }

    public boolean isNeedConfirm() {
        return null == suredotime;//判断是否需要跳出是否确认开始执行任务 null需要不为空不需要
    }

}

