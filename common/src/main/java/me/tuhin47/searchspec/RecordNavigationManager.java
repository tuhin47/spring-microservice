package me.tuhin47.searchspec;

import com.sun.istack.NotNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Log4j2
public class RecordNavigationManager {

    public static final String[] DEFAULT_SORT = {"id", "desc"};

    public static Pageable getPageable(HttpServletRequest request) {
        var all = !Objects.equals(Boolean.FALSE.toString(), request.getParameter("all"));
        int size = all ? Integer.MAX_VALUE : getValueFromRequest(request, "size");
        int page = all ? 0 : getValueFromRequest(request, "page");
        List<Sort.Order> orders = getSortOrders(request);
        var pageRequest = PageRequest.of(page, size, Sort.by(orders));
        log.info("Showing records " + pageRequest);
        return pageRequest;
    }

    public static List<Sort.Order> getSortOrders(HttpServletRequest request) {
        String sorts = request.getParameter("sort");
        if (sorts == null) {
            log.debug("default sort order");
            return Collections.singletonList(new Sort.Order(getSortDirection(DEFAULT_SORT[1]), DEFAULT_SORT[0]));
        }

        var sortFields = sorts.split(";");
        List<Sort.Order> orders = new ArrayList<>();
        for (var sortOrder : sortFields) {
            if (sortOrder.contains(",")) {
                var _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        }
        return orders;
    }

    private static int getValueFromRequest(HttpServletRequest request, @NotNull String param) {
        String paramValue = request.getParameter(param);
        if (paramValue == null) {
            switch (param) {
                case "size":
                    return 10;
                case "page":
                    return 0;
            }
        }
        return Integer.parseInt(paramValue);
    }

    public static Sort.Direction getSortDirection(String direction) {
        return Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);
    }
}