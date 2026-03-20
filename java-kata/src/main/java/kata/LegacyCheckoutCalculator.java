package kata;

public class LegacyCheckoutCalculator {

    public int calculateTotalCents(Order order) {
        int subtotal = order.subtotalCents();
        CustomerType customerType = CustomerType.fromString(order.customerType());
        String country = safe(order.country());
        String coupon = safe(order.couponCode());
        int discountPercent = calculateDiscountPercent(customerType, subtotal, coupon, order.blackFriday());
        int discountedSubtotal = subtotal * (100 - discountPercent) / 100;
        int shippingCents = calculateShippingCents(customerType, country, coupon, discountedSubtotal, order.blackFriday());
        int taxPercent = calculateTaxPercent(customerType, country, coupon);
        int taxCents = discountedSubtotal * taxPercent / 100;
        int total = discountedSubtotal + shippingCents + taxCents;

        if (total < 0) {
            return 0;
        }

        return total;
    }

    private int calculateDiscountPercent(CustomerType customerType, int subtotal, String coupon, boolean blackFriday) {
        int discountPercent = customerType.baseDiscount(subtotal);
        discountPercent += calculateCouponDiscountPercent(customerType, subtotal, coupon);
        discountPercent += customerType.blackFridayDiscount(blackFriday);

        if (discountPercent > 40) {
            return 40;
        }

        return discountPercent;
    }

    private int calculateCouponDiscountPercent(CustomerType customerType, int subtotal, String coupon) {
        if (coupon.equals("SAVE10")) {
            return subtotal >= 5000 ? 10 : 0;
        }

        if (coupon.equals("BULK")) {
            return subtotal >= 20000 ? 7 : 0;
        }

        return customerType.couponDiscount(coupon, subtotal);
    }

    private int calculateShippingCents(CustomerType customerType, String country, String coupon, int discountedSubtotal,
            boolean blackFriday) {
        int shippingCents = baseShippingCents(country);

        if (blackFriday && country.equals("US")) {
            shippingCents += 300;
        }

        if ((coupon.equals("FREESHIP") && discountedSubtotal >= 8000) || customerType.hasFreeShipping(discountedSubtotal)) {
            shippingCents = 0;
        }

        shippingCents += customerType.shippingSurcharge(country);

        return shippingCents;
    }

    private int baseShippingCents(String country) {
        if (country.equals("IT")) {
            return 700;
        }

        if (country.equals("DE")) {
            return 900;
        }

        if (country.equals("US")) {
            return 1500;
        }

        return 2500;
    }

    private int calculateTaxPercent(CustomerType customerType, String country, String coupon) {
        Integer override = customerType.taxOverridePercent(country);
        if (override != null) {
            return override;
        }

        if (country.equals("IT")) {
            return 22;
        }

        if (coupon.equals("TAXFREE")) {
            return 0;
        }

        if (country.equals("DE")) {
            return 19;
        }

        if (country.equals("US")) {
            return 7;
        }

        return 0;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
