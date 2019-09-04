package it_geeks.info.gawla_app.repository.Models;

public class Invoice {

    private int Id;

    private String total;

    private String option_type;

    private String status;

    private String created_at;

    public Invoice(int id, String total, String option_type, String status, String created_at) {
        Id = id;
        this.total = total;
        this.option_type = option_type;
        this.status = status;
        this.created_at = created_at;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOption_type() {
        return option_type;
    }

    public void setOption_type(String option_type) {
        this.option_type = option_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
