package com.hisun.tower.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(name = "接口返回对象", description = "接口返回对象")
public class Result<T> implements Serializable{
    @Serial
    private static final long serialVersionUID = -3212422412048540148L;

    /**
     * 成功标志
     */
    @Schema(name = "成功标志")
    private boolean success = true;

    /**
     * 返回处理消息
     */
    @Schema(name = "返回处理消息")
    private String message = "";

    /**
     * 返回代码
     */
    @Schema(name = "返回代码")
    private Integer code = 0;

    /**
     * 返回数据对象 data
     */
    @Schema(name = "返回数据对象")
    private T result;

    /**
     * 时间戳
     */
    @Schema(name = "时间戳")
    private long timestamp = System.currentTimeMillis();

    public Result() {
    }

    /**
     * 兼容VUE3版token失效不跳转登录页面
     * @param code
     * @param message
     */
    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result<T> success(String message) {
        this.message = message;
        this.code = HttpStatus.OK.value();
        this.success = true;
        return this;
    }

    public static<T> Result<T> ok() {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.OK.value());
        return r;
    }

    public static<T> Result<T> ok(String msg) {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.OK.value());
        //Result OK(String msg)方法会造成兼容性问题 issues/I4IP3D
        r.setResult((T) msg);
        r.setMessage(msg);
        return r;
    }

    public static<T> Result<T> ok(T data) {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.OK.value());
        r.setResult(data);
        return r;
    }

    public static<T> Result<T> OK() {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.OK.value());
        return r;
    }

    /**
     * 此方法是为了兼容升级所创建
     *
     * @param msg
     * @param <T>
     * @return
     */
    public static<T> Result<T> OK(String msg) {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.OK.value());
        r.setMessage(msg);
        //Result OK(String msg)方法会造成兼容性问题 issues/I4IP3D
        r.setResult((T) msg);
        return r;
    }

    public static<T> Result<T> OK(T data) {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.OK.value());
        r.setResult(data);
        return r;
    }

    public static<T> Result<T> OK(String msg, T data) {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(HttpStatus.OK.value());
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static<T> Result<T> error(String msg, T data) {
        Result<T> r = new Result<T>();
        r.setSuccess(false);
        r.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        r.setMessage(msg);
        r.setResult(data);
        return r;
    }

    public static<T> Result<T> error(String msg) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    public static<T> Result<T> error(int code, String msg) {
        Result<T> r = new Result<T>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        return r;
    }

    public Result<T> error500(String message) {
        this.message = message;
        this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.success = false;
        return this;
    }

    /**
     * 无权限访问返回结果
     */
    public static<T> Result<T> noauth(String msg) {
        return error(HttpStatus.UNAUTHORIZED.value(), msg);
    }

    @JsonIgnore
    private String onlTable;
}
