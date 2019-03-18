package it_geeks.info.gawla_app.repository.Models;

public class RoundRemainingTime {

    private  String round_state;
    private int open_hall_value, free_join_value, pay_join_value, first_round_value, first_rest_value, second_round_value, second_rest_value, close_hall_value, last_round_id;
    private boolean open_hall_state, free_join_state, pay_join_state, first_round_state, first_rest_state, second_round_state, second_rest_state, close_hall_state, isUserJoin;

    public RoundRemainingTime(boolean open_hall_state, int open_hall_value, boolean free_join_state, int free_join_value, boolean pay_join_state, int pay_join_value, boolean first_round_state, int first_round_value, boolean first_rest_state, int first_rest_value, boolean second_round_state, int second_round_value, boolean second_rest_state, int second_rest_value, boolean close_hall_state, int close_hall_value, String round_state, boolean isUserJoin, int last_round_id) {
        this.open_hall_state = open_hall_state;
        this.open_hall_value = open_hall_value;
        this.free_join_state = free_join_state;
        this.free_join_value = free_join_value;
        this.pay_join_state = pay_join_state;
        this.pay_join_value = pay_join_value;
        this.first_round_state = first_round_state;
        this.first_round_value = first_round_value;
        this.first_rest_state = first_rest_state;
        this.first_rest_value = first_rest_value;
        this.second_round_state = second_round_state;
        this.second_round_value = second_round_value;
        this.second_rest_state = second_rest_state;
        this.second_rest_value = second_rest_value;
        this.close_hall_state = close_hall_state;
        this.close_hall_value = close_hall_value;
        this.round_state = round_state;
        this.isUserJoin = isUserJoin;
        this.last_round_id = last_round_id;
    }

    public int getLast_round_id() {
        return last_round_id;
    }

    public void setLast_round_id(int last_round_id) {
        this.last_round_id = last_round_id;
    }

    public boolean isUserJoin() {
        return isUserJoin;
    }

    public void setUserJoin(boolean userJoin) {
        isUserJoin = userJoin;
    }

    public String getRound_state() {
        return round_state;
    }

    public void setRound_state(String round_state) {
        this.round_state = round_state;
    }

    public boolean isOpen_hall_state() {
        return open_hall_state;
    }

    public void setOpen_hall_state(boolean open_hall_state) {
        this.open_hall_state = open_hall_state;
    }

    public int getOpen_hall_value() {
        return open_hall_value;
    }

    public void setOpen_hall_value(int open_hall_value) {
        this.open_hall_value = open_hall_value;
    }

    public boolean isFree_join_state() {
        return free_join_state;
    }

    public void setFree_join_state(boolean free_join_state) {
        this.free_join_state = free_join_state;
    }

    public int getFree_join_value() {
        return free_join_value;
    }

    public void setFree_join_value(int free_join_value) {
        this.free_join_value = free_join_value;
    }

    public boolean isPay_join_state() {
        return pay_join_state;
    }

    public void setPay_join_state(boolean pay_join_state) {
        this.pay_join_state = pay_join_state;
    }

    public int getPay_join_value() {
        return pay_join_value;
    }

    public void setPay_join_value(int pay_join_value) {
        this.pay_join_value = pay_join_value;
    }

    public boolean isFirst_round_state() {
        return first_round_state;
    }

    public void setFirst_round_state(boolean first_round_state) {
        this.first_round_state = first_round_state;
    }

    public int getFirst_round_value() {
        return first_round_value;
    }

    public void setFirst_round_value(int first_round_value) {
        this.first_round_value = first_round_value;
    }

    public boolean isFirst_rest_state() {
        return first_rest_state;
    }

    public void setFirst_rest_state(boolean first_rest_state) {
        this.first_rest_state = first_rest_state;
    }

    public int getFirst_rest_value() {
        return first_rest_value;
    }

    public void setFirst_rest_value(int first_rest_value) {
        this.first_rest_value = first_rest_value;
    }

    public boolean isSecond_round_state() {
        return second_round_state;
    }

    public void setSecond_round_state(boolean second_round_state) {
        this.second_round_state = second_round_state;
    }

    public int getSecond_round_value() {
        return second_round_value;
    }

    public void setSecond_round_value(int second_round_value) {
        this.second_round_value = second_round_value;
    }

    public boolean isSecond_rest_state() {
        return second_rest_state;
    }

    public void setSecond_rest_state(boolean second_rest_state) {
        this.second_rest_state = second_rest_state;
    }

    public int getSecond_rest_value() {
        return second_rest_value;
    }

    public void setSecond_rest_value(int second_rest_value) {
        this.second_rest_value = second_rest_value;
    }

    public boolean isClose_hall_state() {
        return close_hall_state;
    }

    public void setClose_hall_state(boolean close_hall_state) {
        this.close_hall_state = close_hall_state;
    }

    public int getClose_hall_value() {
        return close_hall_value;
    }

    public void setClose_hall_value(int close_hall_value) {
        this.close_hall_value = close_hall_value;
    }
}
