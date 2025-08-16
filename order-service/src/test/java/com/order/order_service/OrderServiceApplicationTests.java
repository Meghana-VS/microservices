package com.order.order_service;

import com.order.order_service.model.Order;
import com.order.order_service.repository.OrderRepository;
import com.order.order_service.stubs.InventoryClientStub;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

	@LocalServerPort
	private Integer port;

	@MockitoBean
	private OrderRepository mockOrderRepository;

	@BeforeEach
	void setup(){
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	void shouldPlaceOrder() {

		Order savedOrder = new Order();
		savedOrder.setSkuCode("iphone_15");
		savedOrder.setPrice(new BigDecimal("1200"));
		savedOrder.setQuantity(1);

		when(mockOrderRepository.save(any(Order.class))).thenReturn(savedOrder);

		InventoryClientStub.stubInventoryCall("iphone_15", 1);

		String submitOrderJson = """
				{
				    "skuCode": "iphone_15",
				    "price": "1200",
				    "quantity": 1
				}
				""";

		var responseBodyString = RestAssured.given()
				.contentType("application/json")
				.body(submitOrderJson)
				.when()
				.post("/api/order")
				.then()
				.log().all()
				.statusCode(201)
				.extract()
				.body().asString();

		assertThat(responseBodyString, Matchers.is("Order Placed Successfully"));
	}

}
