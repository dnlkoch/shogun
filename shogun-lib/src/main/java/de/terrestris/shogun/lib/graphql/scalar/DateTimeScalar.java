package de.terrestris.shogun.lib.graphql.scalar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.GraphQLScalarType;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Log4j2
@Component
public class DateTimeScalar extends GraphQLScalarType {

    private static ObjectMapper om;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper){
        DateTimeScalar.om = objectMapper;
    }

    public DateTimeScalar() {
        this("DateTime", "A custom scalar that handles date formats");
    }

    public DateTimeScalar(String name, String description) {
        super(name, description, new Coercing() {
            @Override
            public Object serialize(Object dataFetcherResult) {
                return om.convertValue(dataFetcherResult, String.class);
            }

            @SneakyThrows
            @Override
            public Object parseValue(Object dataFetcherResult) {
                if (dataFetcherResult instanceof String) {
                    String dateTimeString = (String)dataFetcherResult;
                    try {
                        return om.readValue(dateTimeString, Date.class);
                    } catch (JsonProcessingException e) {
                        throw new CoercingParseValueException("Unable to parse variable value " + dataFetcherResult + " as DateTime");
                    }
                }
                throw new CoercingParseValueException("Unable to parse variable value " + dataFetcherResult + " as DateTime");
            }

            @SneakyThrows
            @Override
            public Object parseLiteral(Object dataFetcherResult) {
                if (dataFetcherResult instanceof StringValue) {
                    String dateTimeString = ((StringValue) dataFetcherResult).getValue();;
                    try {
                        return om.readValue(dateTimeString, Date.class);
                    } catch (JsonProcessingException e) {
                        throw new CoercingParseValueException("Unable to parse value " + dataFetcherResult + " as DateTime");
                    }
                }
                throw new CoercingParseLiteralException("Value is not DateTime");
            }
        });
    }

}
