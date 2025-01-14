package me.tuhin47.productservice.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import me.tuhin47.exporter.ExporterType;
import me.tuhin47.payload.response.ProductResponse;
import me.tuhin47.payload.response.ProductsPrice;
import me.tuhin47.productservice.payload.request.ProductRequest;
import me.tuhin47.productservice.payload.response.ProductResponseExporter;
import me.tuhin47.productservice.payload.response.ProductTypeCountReport;
import me.tuhin47.searchspec.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.List;

@Api(tags = "Product API")
public interface ProductController {
    @PreAuthorize("hasAuthority(T(me.tuhin47.utils.RoleUtils).ROLE_USER)")
    @ApiOperation(value = "Add a new product")
    ResponseEntity<String> addProduct(@RequestBody ProductRequest productRequest);

    @PreAuthorize("hasAuthority(T(me.tuhin47.utils.RoleUtils).ROLE_USER)")
    @ApiOperation(value = "Get a product by ID")
    ResponseEntity<ProductResponse> getProductById(@PathVariable("id") String productId);

    @PreAuthorize("hasAuthority(T(me.tuhin47.utils.RoleUtils).ROLE_USER)")
    @ApiOperation(value = "Reduce the quantity of a product")
    ResponseEntity<Void> reduceQuantity(@PathVariable("id") String productId, @RequestParam long quantity);

    @PreAuthorize("hasAuthority(T(me.tuhin47.utils.RoleUtils).ROLE_ADMIN)")
    @ApiOperation(value = "Delete a product by ID")
    ResponseEntity<Void> deleteProductById(@PathVariable("id") String productId);

    @PreAuthorize("hasAuthority(T(me.tuhin47.utils.RoleUtils).ROLE_ADMIN)")
    @ApiOperation(value = "Get All product By Search, Pagination supported")
    ResponseEntity<Page<ProductResponseExporter>> getAllProductBySearch(@RequestBody(required = false) List<SearchCriteria> searchCriteria, @ApiIgnore HttpServletRequest request);

    @PreAuthorize("hasAuthority(T(me.tuhin47.utils.RoleUtils).ROLE_ADMIN)")
    @ApiOperation(value = "Get products count by product type")
    ResponseEntity<List<ProductTypeCountReport>> getProductTypeCount();

    @PreAuthorize("hasAuthority(T(me.tuhin47.utils.RoleUtils).ROLE_ADMIN)")
    @ApiOperation("Export Items")
    ResponseEntity<byte[]> exportExcel(@RequestBody(required = false) List<SearchCriteria> searchCriteria, @RequestParam(value = "type", defaultValue = "EXCEL") ExporterType exporterType, @ApiIgnore HttpServletRequest request) throws IOException;

    @ApiOperation("Get Prices for products")
    ResponseEntity<ProductsPrice> getProductPrices(String[] ids);
}
