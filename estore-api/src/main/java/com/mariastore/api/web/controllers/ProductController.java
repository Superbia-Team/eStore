package com.mariastore.api.web.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.mariastore.core.domain.Product;
import com.mariastore.core.domain.Products;

@Controller
@RequestMapping("/products")
public class ProductController {

	@RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Products> searchAllJSON() {
		List<Product> products = new ArrayList<Product>();
		
		Product testProduct = new Product();
		testProduct.setId(1l);
		testProduct.setSku("TST1");
		testProduct.setName("Test product");
		products.add(testProduct);
		
		Product testProduct2 = new Product();
		testProduct2.setId(2l);
		testProduct2.setSku("TST2");
		testProduct2.setName("Test product 2");
		products.add(testProduct2);
		
        return new ResponseEntity<Products>(new Products(products), HttpStatus.OK);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Product> getById(@PathVariable("id") final Long id, final UriComponentsBuilder uriBuilder, final HttpServletResponse response) {
		Product testProduct = new Product();
		testProduct.setId(id);
		testProduct.setSku("TST1");
		testProduct.setName("Test product");
        return new ResponseEntity<Product>(testProduct, HttpStatus.OK);
    }
	
	@RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody final Product product, final UriComponentsBuilder uriBuilder, final HttpServletResponse response) {
        // RODO: implement
    }
}
