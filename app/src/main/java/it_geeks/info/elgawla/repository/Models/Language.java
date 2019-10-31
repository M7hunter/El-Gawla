package it_geeks.info.elgawla.repository.Models;

public class Language {

    private String Label;

    private String Code;

    public Language(String label, String code) {
        Label = label;
        Code = code;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
