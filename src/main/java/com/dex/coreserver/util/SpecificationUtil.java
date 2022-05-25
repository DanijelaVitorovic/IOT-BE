package com.dex.coreserver.util;

import com.dex.coreserver.exceptions.AppException;
import com.dex.coreserver.model.filter.JoinColumnProps;
import com.dex.coreserver.model.filter.SearchFilter;
import com.dex.coreserver.model.filter.SearchQuery;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpecificationUtil {

    public static <T> Specification<T> bySearchQuery(SearchQuery searchQuery) {

        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criterailBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Predicates for joins
            List<JoinColumnProps> joinColumnProps = searchQuery.getJoinColumnProps();

            if (joinColumnProps != null && !joinColumnProps.isEmpty()) {
                for (JoinColumnProps joinColumnProp : joinColumnProps) {
                    try {
                        addJoinColumnProps(predicates, joinColumnProp, criterailBuilder, root);
                    } catch (ParseException e) {
                        throw new AppException("");
                    }
                }
            }
            // Predicates for root
            List<SearchFilter> searchFilters = searchQuery.getSearchFilter();

            if (searchFilters != null && !searchFilters.isEmpty()) {

                for (final SearchFilter searchFilter : searchFilters) {
                    try {
                        addPredicates(predicates, searchFilter, criterailBuilder, root);
                    } catch (ParseException e) {
                        throw new AppException("");
                    }
                }
            }

            if (predicates.isEmpty()) {
                return criterailBuilder.conjunction();
            }

            return criterailBuilder.and(predicates.toArray(new Predicate[predicates.size()]));

        };
    }

    private static <T> void addJoinColumnProps(List<Predicate> predicates, JoinColumnProps joinColumnProp,
                                               CriteriaBuilder criterailBuilder, Root<T> root) throws ParseException {

        SearchFilter searchFilter = joinColumnProp.getSearchFilter();
        Join<Object, Object> joinParent = root.join(joinColumnProp.getJoinColumnName());

        String property = searchFilter.getProperty();
        Path expression = joinParent.get(property);

        addPredicate(predicates, searchFilter, criterailBuilder, expression);

    }

    private static <T> void addPredicates(List<Predicate> predicates, SearchFilter searchFilter,
                                          CriteriaBuilder criterailBuilder, Root<T> root) throws ParseException {
        String property = searchFilter.getProperty();
        Path expression = root.get(property);

        addPredicate(predicates, searchFilter, criterailBuilder, expression);

    }

    private static void addPredicate(List<Predicate> predicates, SearchFilter searchFilter,
                                     CriteriaBuilder criterailBuilder, Path expression) throws ParseException {
        switch (searchFilter.getOperator()) {
            case "=":
                if(searchFilter.getValue() != "")
                    predicates.add(criterailBuilder.equal(expression, searchFilter.getValue()));
                break;
            case "LIKE":
                predicates.add(criterailBuilder.like(criterailBuilder.upper(expression), "%" + searchFilter.getValue().toString().toUpperCase() + "%"));
                break;
            case "IN":
                predicates.add(criterailBuilder.in(expression).value(searchFilter.getValue()));
                break;
            case ">":
                predicates.add(criterailBuilder.greaterThan(expression, (Comparable) searchFilter.getValue()));
                break;
            case "<":
                predicates.add(criterailBuilder.lessThan(expression, (Comparable) searchFilter.getValue()));
                break;
            case ">=":
                if(expression.getJavaType().equals(Date.class)){
                    predicates.add(criterailBuilder.greaterThanOrEqualTo(expression, new SimpleDateFormat(ApplicationUtils.FILTER_CLIENT_DATE_FORMAT).parse(searchFilter.getValue().toString())));
                }else{
                    predicates.add(criterailBuilder.greaterThanOrEqualTo(expression, (Comparable) searchFilter.getValue()));
                }
                break;
            case "<=":
                if(expression.getJavaType().equals(Date.class)){
                    predicates.add(criterailBuilder.lessThanOrEqualTo(expression, new SimpleDateFormat(ApplicationUtils.FILTER_CLIENT_DATE_FORMAT).parse(searchFilter.getValue().toString())));
                }else {
                    predicates.add(criterailBuilder.lessThanOrEqualTo(expression, (Comparable) searchFilter.getValue()));
                }
                break;
            case "!":
                predicates.add(criterailBuilder.notEqual(expression, searchFilter.getValue()));
                break;
            case "IsNull":
                predicates.add(criterailBuilder.isNull(expression));
                break;
            case "NotNull":
                predicates.add(criterailBuilder.isNotNull(expression));
                break;
            default:
                System.out.println("Predicate is not matched");
                throw new IllegalArgumentException(searchFilter.getOperator() + " is not a valid predicate");
        }

    }

}
