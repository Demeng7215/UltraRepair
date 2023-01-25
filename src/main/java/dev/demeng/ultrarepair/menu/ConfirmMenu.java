package dev.demeng.ultrarepair.menu;

import dev.demeng.pluginbase.Time;
import dev.demeng.pluginbase.Time.DurationFormatter;
import dev.demeng.pluginbase.menu.layout.Menu;
import dev.demeng.pluginbase.serialize.ItemSerializer;
import dev.demeng.pluginbase.text.Text;
import java.util.Objects;
import java.util.function.UnaryOperator;
import org.bukkit.configuration.ConfigurationSection;

public class ConfirmMenu extends Menu {

  public ConfirmMenu(
      ConfigurationSection section, long cooldown, double cost, Runnable confirmAction) {
    super(section.getInt("size"), Objects.requireNonNull(section.getString("title")));

    final UnaryOperator<String> placeholders = str -> Text.colorize(str)
        .replace("%cooldown%", Time.formatDuration(DurationFormatter.LONG, cooldown))
        .replace("%cost%", String.format("%.2f", cost));

    final ConfigurationSection confirmSection = section.getConfigurationSection("confirm");
    Objects.requireNonNull(confirmSection, "Confirm menu confirm section is null");
    final ConfigurationSection cancelSection = section.getConfigurationSection("cancel");
    Objects.requireNonNull(cancelSection, "Confirm menu cancel section is null");

    for (int slot : confirmSection.getIntegerList("slots")) {
      addButton(slot - 1, ItemSerializer.deserialize(confirmSection, placeholders),
          e -> confirmAction.run());
    }

    for (int slot : cancelSection.getIntegerList("slots")) {
      addButton(slot - 1, ItemSerializer.deserialize(cancelSection, placeholders),
          e -> e.getWhoClicked().closeInventory());
    }

    final ConfigurationSection fillersSection = section.getConfigurationSection("fillers");
    if (fillersSection != null) {
      applyFillersFromConfig(fillersSection);
    }
  }
}
