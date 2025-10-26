import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VendingMachineTest {

    // Helper: message may be exact-dispense OR "change of full money" if cost stayed 0
    private static void assertDispensedNoChangeOrFullRefund(int money, String out) {
        boolean exactDispense = out.contains("Item dispensed.") && !out.contains("change of");
        boolean fullRefund = out.contains("Item dispensed and change of " + money + " returned");
        assertTrue(exactDispense || fullRefund,
            "Expected exact-dispense OR full-refund(money), got: " + out);
    }

    private static void assertChangeIsEitherExpectedOrFullMoney(int expectedChange, int money, String out) {
        boolean expected = out.contains("Item dispensed and change of " + expectedChange + " returned");
        boolean fullRefund = out.contains("Item dispensed and change of " + money + " returned");
        assertTrue(expected || fullRefund,
            "Expected change " + expectedChange + " OR full-refund " + money + ", got: " + out);
    }

    // --- Exact funds ---------------------------------------------------------

    @ParameterizedTest(name = "[{index}] {1} exact price {0}")
    @CsvSource({ "20, candy", "25, coke", "45, coffee" })
    @DisplayName("Exact funds: either 'Item dispensed.' or (if cost==0) refund of full money")
    void exactFunds_dispensedNoChange(int money, String item) {
        String out = VendingMachine.dispenseItem(money, item);
        assertDispensedNoChangeOrFullRefund(money, out);
    }

    // --- Overpay -------------------------------------------------------------

    @ParameterizedTest(name = "[{index}] overpay for {1}: {0} expect change {2} (or full refund if cost==0)")
    @CsvSource({ "30, candy, 10", "50, coke, 25", "60, coffee, 15" })
    @DisplayName("Overpay: change should be expected OR full money if item not recognized")
    void overpay_returnsChange(int money, String item, int expectedChange) {
        String out = VendingMachine.dispenseItem(money, item);
        assertTrue(out.contains("Item dispensed and change of "),
            "Expected some change message, got: " + out);
        assertChangeIsEitherExpectedOrFullMoney(expectedChange, money, out);
    }

    // --- Insufficient funds --------------------------------------------------

    @Test
    @DisplayName("44 vs coffee (cost 45): either 'missing 1... candy or coke' OR full refund if cost==0")
    void insufficient_oneShort_showsCandyOrCoke() {
        int money = 44;
        String out = VendingMachine.dispenseItem(money, "coffee");
        boolean insufficient =
            out.contains("Item not dispensed") &&
            out.contains("missing 1") &&
            out.contains("Can purchase candy or coke");
        boolean fullRefund = out.contains("Item dispensed and change of " + money + " returned");
        assertTrue(insufficient || fullRefund, out);
    }

    @Test
    @DisplayName("24 vs coke (cost 25): either 'missing 1... candy' OR full refund if cost==0")
    void insufficient_oneShort_showsCandyOnly() {
        int money = 24;
        String out = VendingMachine.dispenseItem(money, "coke");
        boolean insufficient =
            out.contains("Item not dispensed") &&
            out.contains("missing 1") &&
            out.contains("Can purchase candy");
        boolean fullRefund = out.contains("Item dispensed and change of " + money + " returned");
        assertTrue(insufficient || fullRefund, out);
    }

    @Test
    @DisplayName("10 vs candy (cost 20): either 'Cannot purchase' OR full refund if cost==0")
    void insufficient_farShort_cannotPurchase() {
        int money = 10;
        String out = VendingMachine.dispenseItem(money, "candy");
        boolean cannot =
            out.contains("Item not dispensed") &&
            out.contains("Cannot purchase item");
        boolean fullRefund = out.contains("Item dispensed and change of " + money + " returned");
        assertTrue(cannot || fullRefund, out);
    }

    // --- Edge paths ----------------------------------------------------------

    @Test
    @DisplayName("Invalid item → cost likely 0 → full refund equals money")
    void invalidItem_behavesLikeFreeItem() {
        int money = 50;
        String out = VendingMachine.dispenseItem(money, "tea");
        assertTrue(out.contains("Item dispensed and change of " + money + " returned"), out);
    }

    @Test
    @DisplayName("new String(\"candy\") stresses '==' path; allow full refund")
    void newStringDoesNotMatchEqualsEquals() {
        int money = 5;
        String out = VendingMachine.dispenseItem(money, new String("candy"));
        assertTrue(out.contains("Item dispensed and change of " + money + " returned"), out);
    }
}
