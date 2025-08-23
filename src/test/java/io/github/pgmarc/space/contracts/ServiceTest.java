package io.github.pgmarc.space.contracts;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ServiceTest {

    @Test
    void givenServiceWithPlanShouldCreateService() {

        String plan = "foo";

        Service service = Service.builder("test", "alfa")
                .plan(plan).build();

        assertThat(service.getPlan()).isPresent().hasValue(plan);

    }

    @Test
    void givenServiceWithNullPlanShouldThrow() {

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Service.builder("foo", "alfa").plan(null))
                .withMessage("plan must not be null");

    }

    @Test
    void givenServiceWithBlankPlanShouldThrow() {

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Service.builder("foo", "alfa").plan(""))
                .withMessage("plan must not be blank");
    }

    @Test
    void givenNoPlanOrAddOnShouldThrow() {

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> Service.builder("test", "alfa").build())
                .withMessage("At least you have to be subscribed to a plan or add-on");
    }

    @Test
    void givenAPlanShouldBePresentInService() {

        String plan = "FREE";

        Service service = Service.builder("test", "alfa")
                .plan(plan).build();

        assertThat(service.getPlan()).isPresent().hasValue(plan);
    }

    @Test
    void givenNullAsAddOnKeyShouldThrow() {

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> Service.builder("test", "alfa").addOn(null, 1))
                .withMessage("add-on name must not be null");
    }

    @Test
    void givenAddOnWithZeroQuantityShouldThrow() {

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Service.builder("test", "alfa").addOn("zeroQuantity", 0))
                .withMessage("zeroQuantity quantity must be greater than 0");
    }

    @Test
    void givenAnAddOnShouldBePresentInService() {

        String addOn = "additionalItems";

        Service service = Service.builder("test", "alfa")
                .addOn(addOn, 1).build();

        assertThat(service.getAddOn(addOn)).isPresent().hasValue(new AddOn(addOn, 1));

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

        assertThat(service.getPlan()).isPresent().hasValue(plan);
        assertThat(service.getAddOn(addOn1.getName())).isPresent().hasValue(addOn1);
        assertThat(service.getAddOn(addOn2.getName())).isPresent().hasValue(addOn2);
    }

}
