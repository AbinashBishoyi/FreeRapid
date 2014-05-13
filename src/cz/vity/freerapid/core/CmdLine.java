package cz.vity.freerapid.core;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.DisplaySetting;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.DefaultOption;
import org.apache.commons.cli2.option.PropertyOption;
import org.apache.commons.cli2.util.HelpFormatter;

import java.util.*;

/**
 * @author Vity
 */
final class CmdLine {
    private final MainApp app;
    private Map<String, String> properties = new HashMap<String, String>(2);

    CmdLine(MainApp app) {
        this.app = app;
    }

    private void showVersion() {
        System.out.println(Consts.APPVERSION);
        System.out.println(Consts.AUTHORS);
        app.exit();
    }


    @SuppressWarnings({"unchecked"})
    public List<String> processCommandLine(final String[] args) {
        if (args.length == 0)
            return new LinkedList<String>();
        final DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        //new ArgumentBuilder();
        final GroupBuilder gbuilder = new GroupBuilder();


        final DefaultOption helpOption = obuilder.withShortName("h").withShortName("?").withLongName("help").withDescription("print this message").create();
        final DefaultOption versionOption = obuilder.withShortName("v").withLongName("version").withDescription("print the version information and exit").create();
        final DefaultOption debugOption = obuilder.withShortName("d").withLongName("debug").withDescription("print debugging information").create();

        final PropertyOption propertyOption = new PropertyOption();

        //propertyOption.getDescription()

//        obuilder.withChildren()
//        Option property  = OptionBuilder.withArgName( "property=value" )
//                                .hasArgs()
//                                .withValueSeparator()
//                                .withDescription( "use value for given property" )
//                                .create( "D" );

//        final Argument argument = new ArgumentBuilder().withName("property").withSubsequentSeparator(',').create();

//        final FileValidator fileValidator = new FileValidator();
//        fileValidator.setExisting(true);
//        fileValidator.setDirectory(false);
//        Argument fileArg = abuilder.withName("file").withMinimum(1).withValidator(fileValidator).create();
//        DefaultOption fileOption = obuilder.withRequired(false).withDescription("files to open").withLongName("open").withShortName("o").withArgument(fileArg).create();

        Group options = gbuilder
                .withName("options")
                .withOption(helpOption)
                .withOption(versionOption)
                .withOption(debugOption)
                .withOption(propertyOption)
                .create();
        Parser parser = new Parser();
        parser.setGroup(options);
        try {
            CommandLine cmd = parser.parse(args);

            if (cmd.hasOption(helpOption)) {
                printHelp(options);
            } else if (cmd.hasOption(versionOption)) {
                showVersion();
            } else if (cmd.hasOption(debugOption)) {
                MainApp.debug = true;
            }

            final Set<String> set = (Set<String>) cmd.getProperties(propertyOption);

            for (String o : set) {
                properties.put(o, cmd.getProperty(propertyOption, o, ""));
            }

//            } else if (cmd.hasOption(fileOption)) {
//                return cmd.getValues(fileOption);
//            }
        } catch (OptionException e) {
            printHelp(options);
            System.exit(-1);

        }
        return new LinkedList<String>();
    }

    @SuppressWarnings({"unchecked"})
    private void printHelp(Group options) {
        HelpFormatter f = new HelpFormatter();
        f.getDisplaySettings().add(DisplaySetting.DISPLAY_PROPERTY_OPTION);
        f.setGroup(options);
        f.setShellCommand(Consts.APP_CODE);
        //       f.getFullUsageSettings().add(DisplaySetting.ALL);
//        f.getFullUsageSettings().add(DisplaySetting.DISPLAY_GROUP_NAME);
//        f.getFullUsageSettings().add(DisplaySetting.DISPLAY_GROUP_ARGUMENT);
//        f.getFullUsageSettings().remove(DisplaySetting.DISPLAY_GROUP_EXPANDED);
//        f.getLineUsageSettings().add(DisplaySetting.ALL);
//        f.getLineUsageSettings().add(DisplaySetting.DISPLAY_PARENT_ARGUMENT);
//        f.getLineUsageSettings().add(DisplaySetting.DISPLAY_ARGUMENT_BRACKETED);
        f.getLineUsageSettings().add(DisplaySetting.DISPLAY_PROPERTY_OPTION);
//        f.getLineUsageSettings().add(DisplaySetting.DISPLAY_PARENT_ARGUMENT);
//        f.getLineUsageSettings().add(DisplaySetting.DISPLAY_ARGUMENT_BRACKETED);
        f.setFooter("\nmin. Java version required : 1.6");
        f.print();
        app.exit();
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
