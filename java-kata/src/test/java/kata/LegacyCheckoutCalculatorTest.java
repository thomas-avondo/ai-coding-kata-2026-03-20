package kata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class LegacyCheckoutCalculatorTest {

    private final LegacyCheckoutCalculator calculator = new LegacyCheckoutCalculator();

    @Test
    void calculatesVipTotalWithItalianTaxOverride() {
        assertEquals(10900, totalFor("vip", 10000, "IT", "", false));
    }

    @Test
    void appliesPremiumThresholdDiscounts() {
        assertEquals(11131, totalFor("premium", 9000, "IT", "", false));
        assertEquals(11680, totalFor("premium", 10000, "IT", "", false));
    }

    @Test
    void appliesEmployeeInternationalShippingSurcharge() {
        assertEquals(9730, totalFor("employee", 10000, "DE", "", false));
    }

    @Test
    void doesNotApplyShippingSurchargeForEmployeeInItaly() {
        // employee in IT: no surcharge → base 700, 30% discount, 22% tax
        assertEquals(9240, totalFor("employee", 10000, "IT", "", false));
    }

    @Test
    void appliesZeroDiscountForNewCustomerType() {
        // new customer: same as regular, 0% discount
        assertEquals(12900, totalFor("new", 10000, "IT", "", false));
    }

    @Test
    void appliesSave10OnlyAtThreshold() {
        assertEquals(6798, totalFor("regular", 4999, "IT", "SAVE10", false));
        assertEquals(6190, totalFor("regular", 5000, "IT", "SAVE10", false));
    }

    @Test
    void appliesVipOnlyCouponOnlyForVipCustomers() {
        assertEquals(10300, totalFor("vip", 10000, "IT", "VIPONLY", false));
        assertEquals(12900, totalFor("regular", 10000, "IT", "VIPONLY", false));
    }

    @Test
    void appliesBulkCouponAtThreshold() {
        assertEquals(23034, totalFor("regular", 20000, "DE", "BULK", false));
    }

    @Test
    void addsBlackFridayDiscountAndUsShippingSurcharge() {
        assertEquals(11965, totalFor("regular", 10000, "US", "", true));
    }

    @Test
    void excludesEmployeeFromBlackFridayDiscountButNotUsShippingSurcharge() {
        assertEquals(9790, totalFor("employee", 10000, "US", "", true));
    }

    @Test
    void appliesFreeShipCouponUsingDiscountedSubtotalThreshold() {
        assertEquals(9880, totalFor("vip", 9000, "IT", "FREESHIP", false));
        assertEquals(10200, totalFor("vip", 10000, "IT", "FREESHIP", false));
    }

    @Test
    void appliesVipFreeShippingUsingDiscountedSubtotalThreshold() {
        assertEquals(18698, totalFor("vip", 17647, "IT", "", false));
        assertEquals(18000, totalFor("vip", 17648, "IT", "", false));
    }

    @Test
    void appliesPremiumFreeShippingUsingDiscountedSubtotalThreshold() {
        assertEquals(25098, totalFor("premium", 22222, "IT", "", false));
        assertEquals(24400, totalFor("premium", 22223, "IT", "", false));
    }

    @Test
    void appliesTaxFreeOnlyOutsideItaly() {
        assertEquals(12900, totalFor("regular", 10000, "IT", "TAXFREE", false));
        assertEquals(10900, totalFor("regular", 10000, "DE", "TAXFREE", false));
    }

    @Test
    void capsDiscountAtFortyPercent() {
        assertEquals(4360, totalFor("employee", 5000, "IT", "SAVE10", true));
    }

    @Test
    void appliesPartnerBaseDiscountAndStandardTaxRules() {
        assertEquals(11436, totalFor("partner", 10000, "IT", "", false));
    }

    @Test
    void appliesPartnerCouponOnlyForEligiblePartnerOrders() {
        assertEquals(12851, totalFor("partner", 12000, "IT", "PARTNER5", false));
        assertEquals(13581, totalFor("partner", 11999, "IT", "PARTNER5", false));
        assertEquals(15340, totalFor("regular", 12000, "IT", "PARTNER5", false));
    }

    @Test
    void appliesPartnerBlackFridayBonusInsteadOfStandardFivePercent() {
        assertEquals(13144, totalFor("partner", 12000, "IT", "", true));
    }

    @Test
    void appliesPartnerFreeShippingUsingDiscountedSubtotalThreshold() {
        assertEquals(18998, totalFor("partner", 17045, "IT", "", false));
        assertEquals(18300, totalFor("partner", 17046, "IT", "", false));
    }

    private int totalFor(String customerType, int subtotalCents, String country, String couponCode, boolean blackFriday) {
        return calculator.calculateTotalCents(new Order(customerType, subtotalCents, country, couponCode, blackFriday));
    }
}