package com.ctrip.car.osd.notificationcenter.dao;

import com.ctrip.car.osd.notificationcenter.entity.SDTrackerConfigEntity;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class SDTrackerConfigDao {
    private DalTableDao<SDTrackerConfigEntity> client;

    public SDTrackerConfigDao() throws SQLException {
        this.client = new DalTableDao<>(new DalDefaultJpaParser<>(SDTrackerConfigEntity.class));
    }

    public List<SDTrackerConfigEntity> queryAllConfig() throws SQLException {
        return client.query("SELECT * from sd_tracker_config where isActive = 1", new DalHints());
    }

    public List<SDTrackerConfigEntity> queryById(String[] configId) throws SQLException {
        if (configId.length == 0) return null;
        String placeholders = String.join(",", Collections.nCopies(configId.length, "?"));
        String sql = "SELECT * FROM sd_tracker_config WHERE id IN (" + placeholders + ") AND isActive = 1";

        List<SDTrackerConfigEntity> resultList = client.query(sql, new DalHints(), (Object[]) configId);
        return resultList;
    }
}
