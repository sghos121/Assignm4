import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class VendingMachineTest {

    @ParameterizedTest(name = "[{index}] {1} exact price {0} -> dispensed (no change)")
    @CsvSource({ "20, candy", "25, coke", "45, coffee" })
    @DisplayName("Exact funds: returns 'Item dispensed.'")
    void exactFunds_dispensedNoChange(int money, String item) {
        String out = VendingMachine.dispenseItem(money, item);
        assertTrue(out.contains("Item dispensed."),
                "Expected exact-price purchase to be dispensed, got: " + out);
    }

    @ParameterizedTest(name = "[{index}] overpay for {1}: {0} -> change {2}")
    @CsvSource({ "30, candy, 10", "50, coke, 25", "60, coffee, 15" })
    @DisplayName("Overpay: returns 'Item dispensed and change of X returned'")
    void overpay_returnsChange(int money, String item, int expectedChange) {
        String out = VendingMachine.dispenseItem(money, item);
        assertTrue(out.contains("Item dispensed and change of "), "Expected change message, got: " + out);
        assertTrue(out.contains(Integer.toString(expectedChange)),
                "Expected change " + expectedChange + ", got: " + out);
    }

    @Test
    void insufficient_oneShort_showsCandyOrCoke() {
        String out = VendingMachine.dispenseItem(44, "coffee");
        assertTrue(out.contains("Item not dispensed"), out);
        assertTrue(out.contains("missing 1"), out);
        assertTrue(out.contains("Can purchase candy or coke"), out);
    }

    @Test
    void insufficient_oneShort_showsCandyOnly() {
        String out = VendingMachine.dispenseItem(24, "coke");
        assertTrue(out.contains("Item not dispensed"), out);
        assertTrue(out.contains("missing 1"), out);
        assertTrue(out.contains("Can purchase candy"), out);
    }

    @Test
    void insufficient_farShort_cannotPurchase() {
        String out = VendingMachine.dispenseItem(10, "candy");
        assertTrue(out.contains("Item not dispensed"), out);
        assertTrue(out.contains("Cannot purchase item"), out);
    }

    @Test
    void invalidItem_behavesLikeFreeItem() {
        String out = VendingMachine.dispenseItem(50, "tea");
        assertTrue(out.contains("Item dispensed and change of "), out);
        assertTrue(out.contains("50"), out);
    }

    @Test
    void newStringDoesNotMatchEqualsEquals() {
        String out = VendingMachine.dispenseItem(5, new String("candy"));
        assertTrue(out.contains("Item dispensed and change of "), out);
        assertTrue(out.contains("5"), out);
    }
}

