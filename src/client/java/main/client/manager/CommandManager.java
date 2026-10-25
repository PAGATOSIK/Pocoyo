package main.client.manager;

import main.client.PocoyoVisual;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private String commandPrefix = ".";

    public CommandManager() {
        registerCommands();
    }

    private void registerCommands() {
        registerCommand(new Command("cfg", "Configuration commands") {
            @Override
            public void execute(String[] args) {
                if (args.length == 0) {
                    sendHelp();
                    return;
                }

                switch (args[0].toLowerCase()) {
                    case "save":
                        if (PocoyoVisual.INSTANCE.getCfgManager() != null) {
                            PocoyoVisual.INSTANCE.getCfgManager().saveConfig();
                            sendMessage(Text.literal("§aConfiguration saved!"));
                        }
                        break;
                    case "load":
                        if (PocoyoVisual.INSTANCE.getCfgManager() != null) {
                            PocoyoVisual.INSTANCE.getCfgManager().loadConfig();
                            sendMessage(Text.literal("§aConfiguration loaded!"));
                        }
                        break;
                    default:
                        sendMessage(Text.literal("§cUnknown cfg command. Usage: " + commandPrefix + "cfg [save|load]"));
                }
            }

            @Override
            public void sendHelp() {
                sendMessage(Text.literal("§eConfiguration Commands:"));
                sendMessage(Text.literal("§7" + commandPrefix + "cfg save §f- Save current configuration"));
                sendMessage(Text.literal("§7" + commandPrefix + "cfg load §f- Load configuration from file"));
            }
        });

        registerCommand(new Command("prefix", "Change command prefix") {
            @Override
            public void execute(String[] args) {
                if (args.length == 0) {
                    sendMessage(Text.literal("§cUsage: " + commandPrefix + "prefix <character>"));
                    return;
                }

                String newPrefix = args[0];
                if (newPrefix.length() != 1) {
                    sendMessage(Text.literal("§cPrefix must be a single character!"));
                    return;
                }

                commandPrefix = newPrefix;
                sendMessage(Text.literal("§aCommand prefix changed to: §f" + commandPrefix));
            }

            @Override
            public void sendHelp() {
                sendMessage(Text.literal("§ePrefix Command:"));
                sendMessage(Text.literal("§7" + commandPrefix + "prefix <character> §f- Change command prefix"));
            }
        });

        registerCommand(new Command("help", "Show all commands") {
            @Override
            public void execute(String[] args) {
                sendMessage(Text.literal("§eAvailable Commands:"));
                for (Command cmd : commands.values()) {
                    sendMessage(Text.literal("§7" + commandPrefix + cmd.getName() + " §f- " + cmd.getDescription()));
                }
            }

            @Override
            public void sendHelp() {
                sendMessage(Text.literal("§eHelp Command:"));
                sendMessage(Text.literal("§7" + commandPrefix + "help §f- Show all available commands"));
            }
        });
    }

    private void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }

    public boolean handleCommand(String message) {
        if (message == null || message.isEmpty()) return false;

        if (!message.startsWith(commandPrefix)) return false;

        String commandLine = message.substring(commandPrefix.length()).trim();
        if (commandLine.isEmpty()) return false;

        String[] parts = commandLine.split("\\s+");
        String commandName = parts[0].toLowerCase();
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        Command command = commands.get(commandName);
        if (command != null) {
            command.execute(args);
            return true;
        }

        return false;
    }

    public List<String> getCommandSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();

        if (!input.startsWith(commandPrefix)) return suggestions;

        String inputWithoutPrefix = input.substring(commandPrefix.length()).toLowerCase();

        for (String commandName : commands.keySet()) {
            if (commandName.startsWith(inputWithoutPrefix)) {
                suggestions.add(commandPrefix + commandName);
            }
        }

        return suggestions;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public void setCommandPrefix(String prefix) {
        this.commandPrefix = prefix;
    }

    private void sendMessage(Text message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(message, false);
        }
    }

    public static abstract class Command {
        private final String name;
        private final String description;

        public Command(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public abstract void execute(String[] args);
        public abstract void sendHelp();
    }
}
