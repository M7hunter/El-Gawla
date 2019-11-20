package it_geeks.info.elgawla.repository.Models;

public class RoundRemainingTime {

    private String round_status, round_count, round_date, message;
    private int open_hall_value, free_join_value, pay_join_value, round_value, rest_value, last_round_id;
    private boolean open_hall_state, free_join_state, pay_join_state, round_state, rest_state, close_hall_state, isUserJoin, isToday;

    public RoundRemainingTime(boolean open_hall_state, int open_hall_value, boolean free_join_state, int free_join_value, boolean pay_join_state, int pay_join_value, boolean close_hall_state, boolean round_state, int round_value, boolean rest_state, int rest_value, String round_count, String status, String msg, String round_date, boolean isToday, int last_round_id, boolean isUserJoin) {
        this.open_hall_state = open_hall_state;
        this.open_hall_value = open_hall_value;
        this.free_join_state = free_join_state;
        this.free_join_value = free_join_value;
        this.pay_join_state = pay_join_state;
        this.pay_join_value = pay_join_value;
        this.close_hall_state = close_hall_state;
        this.round_state = round_state;
        this.round_value = round_value;
        this.rest_state = rest_state;
        this.rest_value = rest_value;
        this.round_count = round_count;
        this.round_status = status;
        this.message = msg;
        this.isToday = isToday;
        this.round_date = round_date;
        this.last_round_id = last_round_id;
        this.isUserJoin = isUserJoin;
    }

    public int getLast_round_id() {
        return last_round_id;
    }

    public boolean isUserJoin() {
        return isUserJoin;
    }

    public void setUserJoin(boolean userJoin) {
        isUserJoin = userJoin;
    }

    public String getRound_status() {
        return round_status;
    }

    public boolean isOpen_hall_state() {
        return open_hall_state;
    }

    public int getOpen_hall_value() {
        return open_hall_value;
    }

    public boolean isFree_join_state() {
        return free_join_state;
    }

    public int getFree_join_value() {
        return free_join_value;
    }

    public boolean isPay_join_state() {
        return pay_join_state;
    }

    public int getPay_join_value() {
        return pay_join_value;
    }

    public int getRound_value() {
        return round_value;
    }

    public void setRound_value(int round_value) {
        this.round_value = round_value;
    }

    public int getRest_value() {
        return rest_value;
    }

    public void setRest_value(int rest_value) {
        this.rest_value = rest_value;
    }

    public boolean isRound_state() {
        return round_state;
    }

    public void setRound_state(boolean round_state) {
        this.round_state = round_state;
    }

    public boolean isRest_state() {
        return rest_state;
    }

    public void setRest_state(boolean rest_state) {
        this.rest_state = rest_state;
    }

    public String getRound_count() {
        return round_count;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRound_count(String round_count) {
        this.round_count = round_count;
    }

    public String getRound_date() {
        return round_date;
    }

    public void setRound_date(String round_date) {
        this.round_date = round_date;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public boolean isClose_hall_state() {
        return close_hall_state;
    }
}
