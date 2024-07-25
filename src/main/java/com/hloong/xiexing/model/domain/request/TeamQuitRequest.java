package com.hloong.xiexing.model.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamQuitRequest implements Serializable {


    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * id
     */
    private Long teamId;

}
