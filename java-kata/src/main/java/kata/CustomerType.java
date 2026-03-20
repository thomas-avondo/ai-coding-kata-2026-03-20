package kata;

public enum CustomerType {
    VIP {
        @Override
        public int baseDiscount(int subtotal) { return 15; }
        @Override
        public int couponDiscount(String coupon, int subtotal) { return "VIPONLY".equals(coupon) ? 5 : 0; }
        @Override
        public boolean hasFreeShipping(int discountedSubtotal) { return discountedSubtotal >= 15000; }
        @Override
        public Integer taxOverridePercent(String country) { return "IT".equals(country) ? 20 : null; }
    },
    PREMIUM {
        @Override
        public int baseDiscount(int subtotal) { return subtotal >= 10000 ? 10 : 5; }
        @Override
        public boolean hasFreeShipping(int discountedSubtotal) { return discountedSubtotal >= 20000; }
    },
    EMPLOYEE {
        @Override
        public int baseDiscount(int subtotal) { return 30; }
        @Override
        public int blackFridayDiscount(boolean blackFriday) { return 0; }
        @Override
        public int shippingSurcharge(String country) { return "IT".equals(country) ? 0 : 500; }
    },
    PARTNER {
        @Override
        public int baseDiscount(int subtotal) { return 12; }
        @Override
        public int couponDiscount(String coupon, int subtotal) { return "PARTNER5".equals(coupon) && subtotal >= 12000 ? 5 : 0; }
        @Override
        public int blackFridayDiscount(boolean blackFriday) { return blackFriday ? 3 : 0; }
        @Override
        public boolean hasFreeShipping(int discountedSubtotal) { return discountedSubtotal >= 15000; }
    },
    REGULAR;

    public int baseDiscount(int subtotal) { return 0; }
    public int couponDiscount(String coupon, int subtotal) { return 0; }
    public int blackFridayDiscount(boolean blackFriday) { return blackFriday ? 5 : 0; }
    public boolean hasFreeShipping(int discountedSubtotal) { return false; }
    public int shippingSurcharge(String country) { return 0; }
    public Integer taxOverridePercent(String country) { return null; }

    public static CustomerType fromString(String type) {
        if (type == null) return REGULAR;
        switch (type.trim().toLowerCase()) {
            case "vip": return VIP;
            case "premium": return PREMIUM;
            case "employee": return EMPLOYEE;
            case "partner": return PARTNER;
            default: return REGULAR;
        }
    }
}
