package net.changami.app.lunchdecider.data;

import java.util.Date;

/**
 * Created by chan_gami on 2014/05/16.
 */
public class LunchPointEntity {

    private int _id;
    private String pointName;
    private Date lastTime;


    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
