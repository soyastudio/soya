package soya.framework.action.support;

import java.io.Serializable;
import java.util.*;

public class Options implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Option> shortOpts = new LinkedHashMap<>();
    private final Map<String, Option> longOpts = new LinkedHashMap<>();

    /**
     * a map of the required options
     */
    // N.B. This can contain either a String (addOption) or an OptionGroup (addOptionGroup)
    // TODO this seems wrong
    private final List<Object> requiredOpts = new ArrayList<>();

    private final Map<String, OptionGroup> optionGroups = new LinkedHashMap<>();

    public Options addOption(final Option opt) {
        final String key = opt.getKey();

        // add it to the long option list
        if (opt.hasLongOpt()) {
            longOpts.put(opt.getLongOpt(), opt);
        }

        // if the option is required add it to the required list
        if (opt.isRequired()) {
            if (requiredOpts.contains(key)) {
                requiredOpts.remove(requiredOpts.indexOf(key));
            }
            requiredOpts.add(key);
        }

        shortOpts.put(key, opt);

        return this;
    }

    public Options addOption(final String opt, final boolean hasArg, final String description) {
        addOption(opt, null, hasArg, description);
        return this;
    }

    public Options addOption(final String opt, final String description) {
        addOption(opt, null, false, description);
        return this;
    }

    public Options addOption(final String opt, final String longOpt, final boolean hasArg, final String description) {
        addOption(new Option(opt, longOpt, hasArg, description));
        return this;
    }

    public Options addOptionGroup(final OptionGroup group) {
        if (group.isRequired()) {
            requiredOpts.add(group);
        }

        for (final Option option : group.getOptions()) {
            // an Option cannot be required if it is in an
            // OptionGroup, either the group is required or
            // nothing is required
            option.setRequired(false);
            addOption(option);

            optionGroups.put(option.getKey(), group);
        }

        return this;
    }

    public Options addRequiredOption(final String opt, final String longOpt, final boolean hasArg, final String description) {
        final Option option = new Option(opt, longOpt, hasArg, description);
        option.setRequired(true);
        addOption(option);
        return this;
    }

    public List<String> getMatchingOptions(String opt) {
        opt = Util.stripLeadingHyphens(opt);

        final List<String> matchingOpts = new ArrayList<>();

        // for a perfect match return the single option only
        if (longOpts.containsKey(opt)) {
            return Collections.singletonList(opt);
        }

        for (final String longOpt : longOpts.keySet()) {
            if (longOpt.startsWith(opt)) {
                matchingOpts.add(longOpt);
            }
        }

        return matchingOpts;
    }

    public Option getOption(String opt) {
        opt = Util.stripLeadingHyphens(opt);

        if (shortOpts.containsKey(opt)) {
            return shortOpts.get(opt);
        }

        return longOpts.get(opt);
    }

    public OptionGroup getOptionGroup(final Option opt) {
        return optionGroups.get(opt.getKey());
    }

    Collection<OptionGroup> getOptionGroups() {
        return new HashSet<>(optionGroups.values());
    }

    public Collection<Option> getOptions() {
        return Collections.unmodifiableCollection(helpOptions());
    }

    public List getRequiredOptions() {
        return Collections.unmodifiableList(requiredOpts);
    }

    public boolean hasLongOption(String opt) {
        opt = Util.stripLeadingHyphens(opt);

        return longOpts.containsKey(opt);
    }

    public boolean hasOption(String opt) {
        opt = Util.stripLeadingHyphens(opt);

        return shortOpts.containsKey(opt) || longOpts.containsKey(opt);
    }

    public boolean hasShortOption(String opt) {
        opt = Util.stripLeadingHyphens(opt);

        return shortOpts.containsKey(opt);
    }

    List<Option> helpOptions() {
        return new ArrayList<>(shortOpts.values());
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();

        buf.append("[ Options: [ short ");
        buf.append(shortOpts.toString());
        buf.append(" ] [ long ");
        buf.append(longOpts);
        buf.append(" ]");

        return buf.toString();
    }
}
