package it_geeks.info.gawla_app.Repositry.Models;

import java.util.List;

public class ResponseMainBody {

    private boolean status;

    private int code;

    private String message;

    private List<Round> data;

    private List<String> errors;

    public ResponseMainBody(boolean status, int code, List<String> errors) { // error body
        this.status = status;
        this.code = code;
        this.errors = errors;
    }

    public ResponseMainBody(boolean status, int code, String message, List<Round> rounds) { // all hales body
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = rounds;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Round> getData() {
        return data;
    }

    public void setData(List<Round> data) {
        this.data = data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
