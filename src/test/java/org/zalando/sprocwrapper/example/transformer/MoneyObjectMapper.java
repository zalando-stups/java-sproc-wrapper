package org.zalando.sprocwrapper.example.transformer;

import org.zalando.sprocwrapper.example.OrderMonetaryAmount;
import org.zalando.sprocwrapper.example.OrderMonetaryAmountImpl;
import org.zalando.sprocwrapper.globalobjecttransformer.annotation.GlobalObjectMapper;
import org.zalando.typemapper.core.fieldMapper.ObjectMapper;
import org.zalando.typemapper.core.result.DbResultNode;
import org.zalando.typemapper.postgres.PgTypeHelper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * @author  danieldelhoyo
 */
@GlobalObjectMapper
public class MoneyObjectMapper extends ObjectMapper<OrderMonetaryAmount> {

    public MoneyObjectMapper() {
        super(OrderMonetaryAmount.class);
    }

    @Override
    public OrderMonetaryAmount unmarshalFromDbNode(final DbResultNode dbResultNode) {
        List<DbResultNode> dbResultNodeList = dbResultNode.getChildren();
        BigDecimal amount = new BigDecimal(dbResultNodeList.get(0).getValue());
        String currency = dbResultNodeList.get(1).getValue();

        return new OrderMonetaryAmountImpl(amount, currency);
    }

    @Override
    public PgTypeHelper.PgTypeDataHolder marshalToDb(final OrderMonetaryAmount t) {
        TreeMap<Integer, Object> resultPositionMap = new TreeMap<Integer, Object>();
        resultPositionMap.put(1, t.getAmount());
        resultPositionMap.put(2, t.getCurrency());
        return new PgTypeHelper.PgTypeDataHolder("monetary_amount", Collections.unmodifiableCollection(resultPositionMap.values()));
    }

}
