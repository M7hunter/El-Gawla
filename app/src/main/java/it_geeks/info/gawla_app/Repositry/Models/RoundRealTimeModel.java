package it_geeks.info.gawla_app.Repositry.Models;

public class RoundRealTimeModel {

    boolean open_hall_status , free_join_status , pay_join_status , first_round_status , first_rest_status , seconed_round_status , seconed_rest_status , close_hall_status ;
    int open_hall_value ,free_join_value ,pay_join_value ,first_round_value ,first_rest_value , seconed_round_value ,seconed_rest_value , close_hall_value;
    String round_status;

    public RoundRealTimeModel() {
    }

    public RoundRealTimeModel(boolean open_hall_status, int open_hall_value, boolean free_join_status, int free_join_value, boolean pay_join_status, int pay_join_value, boolean first_round_status, int first_round_value, boolean first_rest_status, int first_rest_value, boolean seconed_round_status, int seconed_round_value, boolean seconed_rest_status, int seconed_rest_value, boolean close_hall_status, int close_hall_value ,String round_status) {
        this.open_hall_status = open_hall_status;
        this.open_hall_value = open_hall_value;
        this.free_join_status = free_join_status;
        this.free_join_value = free_join_value;
        this.pay_join_status = pay_join_status;
        this.pay_join_value = pay_join_value;
        this.first_round_status = first_round_status;
        this.first_round_value = first_round_value;
        this.first_rest_status = first_rest_status;
        this.first_rest_value = first_rest_value;
        this.seconed_round_status = seconed_round_status;
        this.seconed_round_value = seconed_round_value;
        this.seconed_rest_status = seconed_rest_status;
        this.seconed_rest_value = seconed_rest_value;
        this.close_hall_status = close_hall_status;
        this.close_hall_value = close_hall_value;
        this.round_status = round_status;
    }

    public String getRound_status() {
        return round_status;
    }

    public void setRound_status(String round_status) {
        this.round_status = round_status;
    }

    public boolean isOpen_hall_status() {
        return open_hall_status;
    }

    public void setOpen_hall_status(boolean open_hall_status) {
        this.open_hall_status = open_hall_status;
    }

    public int getOpen_hall_value() {
        return open_hall_value;
    }

    public void setOpen_hall_value(int open_hall_value) {
        this.open_hall_value = open_hall_value;
    }

    public boolean isFree_join_status() {
        return free_join_status;
    }

    public void setFree_join_status(boolean free_join_status) {
        this.free_join_status = free_join_status;
    }

    public int getFree_join_value() {
        return free_join_value;
    }

    public void setFree_join_value(int free_join_value) {
        this.free_join_value = free_join_value;
    }

    public boolean isPay_join_status() {
        return pay_join_status;
    }

    public void setPay_join_status(boolean pay_join_status) {
        this.pay_join_status = pay_join_status;
    }

    public int getPay_join_value() {
        return pay_join_value;
    }

    public void setPay_join_value(int pay_join_value) {
        this.pay_join_value = pay_join_value;
    }

    public boolean isFirst_round_status() {
        return first_round_status;
    }

    public void setFirst_round_status(boolean first_round_status) {
        this.first_round_status = first_round_status;
    }

    public int getFirst_round_value() {
        return first_round_value;
    }

    public void setFirst_round_value(int first_round_value) {
        this.first_round_value = first_round_value;
    }

    public boolean isFirst_rest_status() {
        return first_rest_status;
    }

    public void setFirst_rest_status(boolean first_rest_status) {
        this.first_rest_status = first_rest_status;
    }

    public int getFirst_rest_value() {
        return first_rest_value;
    }

    public void setFirst_rest_value(int first_rest_value) {
        this.first_rest_value = first_rest_value;
    }

    public boolean isSeconed_round_status() {
        return seconed_round_status;
    }

    public void setSeconed_round_status(boolean seconed_round_status) {
        this.seconed_round_status = seconed_round_status;
    }

    public int getSeconed_round_value() {
        return seconed_round_value;
    }

    public void setSeconed_round_value(int seconed_round_value) {
        this.seconed_round_value = seconed_round_value;
    }

    public boolean isSeconed_rest_status() {
        return seconed_rest_status;
    }

    public void setSeconed_rest_status(boolean seconed_rest_status) {
        this.seconed_rest_status = seconed_rest_status;
    }

    public int getSeconed_rest_value() {
        return seconed_rest_value;
    }

    public void setSeconed_rest_value(int seconed_rest_value) {
        this.seconed_rest_value = seconed_rest_value;
    }

    public boolean isClose_hall_status() {
        return close_hall_status;
    }

    public void setClose_hall_status(boolean close_hall_status) {
        this.close_hall_status = close_hall_status;
    }

    public int getClose_hall_value() {
        return close_hall_value;
    }

    public void setClose_hall_value(int close_hall_value) {
        this.close_hall_value = close_hall_value;
    }
}
