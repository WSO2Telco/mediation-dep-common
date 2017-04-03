package com.wso2telco.dep.common.mediation.spendlimit;


public class CustomException extends RuntimeException {

    private String errcode;
    private String errmsg;
    private String[] errvar;


    public CustomException(String errcode, String errmsg, String[] errvar) {
        super(errcode);
        this.errcode = errcode;
        this.errmsg = errmsg;
        this.errvar = errvar;
    }


    public String getErrcode() {
        return errcode;
    }

    public String[] getErrvar() {
        return errvar;
    }

    /**
     * @return the errmsg
     */
    public String getErrmsg() {
        return errmsg;
    }

    /**
     * @param errmsg the errmsg to set
     */
    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

}
