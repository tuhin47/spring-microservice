package me.tuhin47.productservice.conttoller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.tuhin47.exporter.ExporterType;
import me.tuhin47.productservice.payload.request.ProductRequest;
import me.tuhin47.productservice.payload.response.ProductResponse;
import me.tuhin47.searchspec.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Api(tags = "Product API")
@RequestMapping("/product")
public interface ProductController {
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    @ApiOperation(value = "Add a new product")
    ResponseEntity<Long> addProduct(@RequestBody ProductRequest productRequest);

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{id}")
    @ApiOperation(value = "Get a product by ID")
    ResponseEntity<ProductResponse> getProductById(@PathVariable("id") long productId);

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/reduceQuantity/{id}")
    @ApiOperation(value = "Reduce the quantity of a product")
    ResponseEntity<Void> reduceQuantity(@PathVariable("id") long productId, @RequestParam long quantity);

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a product by ID")
    void deleteProductById(@PathVariable("id") long productId);

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/all", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "Get All product By Search, Pagination supported")
    ResponseEntity<Page<ProductResponse>> getAllProductBySearch(@RequestBody(required = false) List<SearchCriteria> searchCriteria, @ApiIgnore HttpServletRequest request);

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/excel",method = {RequestMethod.GET,RequestMethod.POST})
    @ApiOperation("Export Items")
    ResponseEntity<byte[]> exportExcel(@RequestBody(required = false) List<SearchCriteria> searchCriteria,@RequestParam(value = "type",defaultValue = "EXCEL") ExporterType exporterType, @ApiIgnore HttpServletRequest request) throws IOException;
}