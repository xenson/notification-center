package com.ctrip.car.osd.notificationcenter.basic;

import com.ctrip.car.osd.notificationcenter.config.QCHickwall;
import com.dianping.cat.Cat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;

import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by xiayx on 2021/5/31.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ObjectUtils.class, Cat.class, QCHickwall.class})
public class ObjectUtilsTest {

    @Before
    public void init() throws Exception {
        mockStatic(QCHickwall.class);
        when(QCHickwall.getList("Blueprint_HickwallTrue")).thenReturn(Arrays.asList("1", "true", "yes"));
        when(QCHickwall.getList("Blueprint_HickwallFalse")).thenReturn(Arrays.asList("0", "false", "no"));
    }

    @Test
    public void convertBoolToInt_test() {
        String resultTrue = ObjectUtils.convertToTag("true", "0");
        Assert.assertTrue(resultTrue.equals("1"));

        String resultFalse = ObjectUtils.convertToTag("false", "0");
        Assert.assertTrue(resultFalse.equals("0"));

        String resultDefault = ObjectUtils.convertToTag("test", "0");
        Assert.assertTrue(resultDefault.equals("0"));

        String resultInt1 = ObjectUtils.convertToTag("true");
        Assert.assertTrue(resultInt1.equals("1"));

        String resultInt0 = ObjectUtils.convertToTag("false");
        Assert.assertTrue(resultInt0.equals("0"));

        String resultStr = ObjectUtils.convertToTag("test");
        Assert.assertTrue(resultStr.equals("test"));
    }

    @Test
    public void convertToLong_test() {
        Long result = ObjectUtils.convertToLong("100.0");
        Assert.assertTrue(result.equals(100L));

        Long resultNull = ObjectUtils.convertToLong("");
        Assert.assertTrue(resultNull.equals(0L));
    }

    @Test
    public void convertNumeric_test() {
        Long result = ObjectUtils.convertNumeric("100");
        Assert.assertTrue(result.equals(100L));

        Long resultNull = ObjectUtils.convertNumeric(null);
        Assert.assertTrue(resultNull.equals(0L));
    }
}
