package org.epcc.ps.client.shell.command;

import com.google.gson.Gson;
import org.apache.commons.cli.*;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.epcc.ps.client.shell.exception.PPMFileException;
import org.epcc.ps.client.shell.exception.SimulationSourceNotFoundException;
import org.epcc.ps.client.shell.service.ConvertService;
import org.epcc.ps.core.entity.creature.Species;
import org.epcc.ps.core.entity.environment.Landscape;
import org.epcc.ps.core.evolution.LandscapeEvolutionManager;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author shaohan.yin
 * Created on 24/10/2017
 */
public class DefaultSimulationCommand extends AbstractCommand implements SimulationCommand {
    private static final String COMMAND_NAME = "simulate";
    private static final String SIMULATION_SOURCE_FLAG = "f";
    private static final String SIMULATION_SOURCE_FLAG_LONG = "file";
    private static final String SIMULATION_OUTPUT_INTERVAL_FLAG = "i";
    private static final String SIMULATION_OUTPUT_INTERVAL_FLAG_LONG = "interval";
    private static final String SIMULATION_REPORT_FLAG = "r";
    private static final String SIMULATION_REPORT_FLAG_LONG = "report";
    private static final String VELOCITY_TEMPLATE_FILE = "report-template.vm";

    private ConvertService convertService = ConvertService.DEFAULT;
    private Gson gson = new Gson();

    private CommandLineParser parser;
    private Options options;
    private HelpFormatter formatter;

    public DefaultSimulationCommand() {
        super();

        parser = new DefaultParser();
        options = new Options();
        formatter = new HelpFormatter();

        options.addOption(SIMULATION_SOURCE_FLAG, SIMULATION_SOURCE_FLAG_LONG,
                true, "Specified landscape generation file source.");
        options.addOption(SIMULATION_OUTPUT_INTERVAL_FLAG, SIMULATION_OUTPUT_INTERVAL_FLAG_LONG,
                true, "(Optional) PPM output interval.Default value is 100.");
        options.addOption(SIMULATION_REPORT_FLAG, SIMULATION_REPORT_FLAG_LONG,
                false, "(Optional) Generate report.");
    }

    protected DefaultSimulationCommand(ConvertService convertService) {
        this();
        this.convertService = convertService;
    }

    @Override
    public void simulate(String[] args) {
        try {
            CommandLine commandLine = parser.parse(options, args);
            check(commandLine);

            String fileSource = commandLine.getOptionValue(SIMULATION_SOURCE_FLAG);
            int interval = Integer.parseInt(
                    commandLine.getOptionValue(SIMULATION_OUTPUT_INTERVAL_FLAG, "100"));

            Landscape landscape = convertService.convertLandscapeFromFile(fileSource);
            LandscapeEvolutionManager landscapeEvolutionManager = LandscapeEvolutionManager.create(landscape);
            landscapeEvolutionManager.evolution();

            generateResult(landscapeEvolutionManager.getSnapshots(), interval);

            if (commandLine.hasOption(SIMULATION_REPORT_FLAG)) {
                generateReport(landscapeEvolutionManager.getSnapshots());
            }

        } catch (Exception e) {
            logger.error("Simulation failed.", e);
            formatter.printHelp(COMMAND_NAME, options);
        }
    }

    private void check(CommandLine commandLine) throws SimulationSourceNotFoundException {
        if (!commandLine.hasOption(SIMULATION_SOURCE_FLAG)) {
            throw new SimulationSourceNotFoundException("Simulation source file must be specified with -f flag.");
        }
    }

    private void generateResult(List<Landscape> landscapes, int interval) throws PPMFileException {
        for (int idx = 0; idx <= landscapes.size(); idx += interval) {
            convertService.convertLandscapeWithSpeciesToPPM(String.format("%d-hare.ppm", idx),
                    landscapes.get(idx), Species.HARE);
            convertService.convertLandscapeWithSpeciesToPPM(String.format("%d-puma.ppm", idx),
                    landscapes.get(idx), Species.PUMA);

            logger.info(String.format("Average Density %d: PUMA %.3f HARE %.3f", idx,
                    calculateAverageDensity(landscapes.get(idx), Species.PUMA),
                    calculateAverageDensity(landscapes.get(idx), Species.HARE)));
        }
    }

    private double calculateAverageDensity(Landscape landscape, Species species) {
        double result = 0;
        for (int xIdx = 0; xIdx != landscape.getLength(); ++xIdx) {
            for (int yIdx = 0; yIdx != landscape.getWidth(); ++yIdx) {
                result += landscape.getGrids()[xIdx][yIdx].getDensity(species);
            }
        }
        return result / (landscape.getLength() * landscape.getWidth());
    }

    private void generateReport(List<Landscape> landscapes) {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();

        Template template = velocityEngine.getTemplate(VELOCITY_TEMPLATE_FILE, "UTF-8");

        VelocityContext context = new VelocityContext();
        context.put("densities", gson.toJson(generateDensityDataForReport(landscapes)));
        context.put("hares", gson.toJson(generateSpeciesDataForReport(landscapes, Species.HARE)));
        context.put("pumas", gson.toJson(generateSpeciesDataForReport(landscapes, Species.PUMA)));

        try (FileWriter fileWriter = new FileWriter("report.html")) {
            StringWriter stringWriter = new StringWriter();
            template.merge(context, stringWriter);
            fileWriter.write(stringWriter.toString());
        } catch (IOException e) {
            logger.error("Cannot write report to file.", e);
        }
    }

    private Map<String, List<Double>> generateDensityDataForReport(List<Landscape> landscapes) {
        Map<String, List<Double>> averageDensities = new HashMap<>(2);
        List<Double> hares = new LinkedList<>();
        List<Double> pumas = new LinkedList<>();
        averageDensities.put(Species.HARE.getSpeciesName(), hares);
        averageDensities.put(Species.PUMA.getSpeciesName(), pumas);
        landscapes.forEach((landscape) -> {
            hares.add(calculateAverageDensity(landscape, Species.HARE));
            pumas.add(calculateAverageDensity(landscape, Species.PUMA));
        });
        return averageDensities;
    }

    private List<Double>[][] generateSpeciesDataForReport(List<Landscape> landscapes, Species species) {
        int length = landscapes.get(0).getLength();
        int width = landscapes.get(0).getWidth();
        List<Double>[][] data = new List[length][width];

        for (Landscape landscape : landscapes) {
            for (int xIdx = 0; xIdx != landscape.getLength(); ++xIdx) {
                for (int yIdx = 0; yIdx != landscape.getWidth(); ++yIdx) {
                    if (null == data[xIdx][yIdx]) {
                        data[xIdx][yIdx] = new LinkedList<>();
                    }
                    data[xIdx][yIdx].add(landscape.getGrids()[xIdx][yIdx].getDensity(species));
                }
            }
        }

        return data;
    }
}
