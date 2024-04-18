package com.semmtech.laces.fetch.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.runner.RunWith;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(importOptions = {ImportOption.DoNotIncludeTests.class, ImportOption.DoNotIncludeJars.class}, packages = "com.semmtech.laces.fetch")
public class ApplicationLayersTest {

    /**
     * Make sure services are called from higher level layers and not from the entities/repository layer.
     */
    @ArchTest
    public static final ArchRule servicesAccessedOnlyFromHigherLayers =
            classes()
                    .that().resideInAPackage("..service..")
                    .should().onlyBeAccessed().byAnyPackage("..rest..", "..service..", "..facade..", "..config..");

    /**
     * Make sure repositories are only called from the service layer
     */
    @ArchTest
    public static ArchRule repositoriesOnlyAccessedFromServiceLayer =
            classes()
                    .that().resideInAPackage("..repository..")
                    .should().onlyBeAccessed().byAnyPackage("..service..");

}
