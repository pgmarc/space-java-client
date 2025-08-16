package io.github.pgmarc.space.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ServiceTest {

    @Test
    void givenServiceWithPlanShouldCreateService() {

        String plan = "foo";

        Service service = Service.builder("test", "alfa")
                .plan(plan).build();

        assertEquals("foo", service.getPlan().get());
    }

    @Test
    void givenServiceWithNullPlanShouldThrow() {
        Exception ex = assertThrows(NullPointerException.class,
                () -> Service.builder("foo", "alfa").plan(null));

        assertEquals("plan must not be null", ex.getMessage());
    }

    @Test
    void givenServiceWithBlankPlanShouldThrow() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> Service.builder("foo", "alfa").plan(""));

        assertEquals("plan must not be blank", ex.getMessage());
    }

    @Test
    void givenNoPlanOrAddOnShouldThrow() {

        Exception ex = assertThrows(IllegalStateException.class,
                () -> Service.builder("test", "alfa").build());
        assertEquals("At least you have to be subscribed to a plan or add-on", ex.getMessage());
    }

    @Test
    void givenAPlanShouldBePresentInService() {

        String plan = "FREE";

        Service service = Service.builder("test", "alfa")
                .plan(plan).build();

        assertEquals(plan, service.getPlan().get());
    }

    @Test
    void givenNullAsAddOnKeyShouldThrow() {
        Exception ex = assertThrows(NullPointerException.class,
                () -> Service.builder("test", "alfa").addOn(null, 1));

        assertEquals("add-on name must not be null", ex.getMessage());
    }

    
    @Test
    void givenAddOnWithZeroQuantityShouldThrow() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> Service.builder("test", "alfa").addOn("zeroQuantity", 0));

        assertEquals("zeroQuantity quantity must be greater than 0", ex.getMessage());
    }

    @Test
    void givenAnAddOnShouldBePresentInService() {

        String addOn = "additionalItems";

        Service service = Service.builder("test", "alfa")
                .addOn(addOn, 1).build();

        assertEquals(addOn, service.getAddOn(addOn).get().getName());
    }

    @Test
    void givenPlanAndAddOnsShouldBePresent() {
        String plan = "FREE";
        AddOn addOn1 = new AddOn("addOn1", 1);
        AddOn addOn2 = new AddOn("addOn2", 2);

        Service service = Service.builder("test", "alfa")
                .plan(plan)
                .addOn(addOn1.getName(), addOn1.getQuantity())
                .addOn(addOn2.getName(), addOn1.getQuantity()).build();

        assertEquals(plan, service.getPlan().get());
        assertEquals(addOn1, service.getAddOn(addOn1.getName()).get());
        assertEquals(addOn2, service.getAddOn(addOn2.getName()).get());
    }

}
