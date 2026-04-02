package io.mosip.registration.util.control.impl;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.controller.GenericController;
import io.mosip.registration.dto.mastersync.GenericDto;
import io.mosip.registration.util.control.FxControl;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import java.util.List;

public class CountryDropdownFxControl extends DropDownFxControl {

    private static final Logger LOGGER = AppConfig.getLogger(CountryDropdownFxControl.class);
    private static final String LOGGER_CLASS_NAME = "CountryDropdownFxControl";

    @Override
    public void setListener(Node node) {

        super.setListener(node);

        ComboBox<GenericDto> fieldComboBox = (ComboBox<GenericDto>) node;

        fieldComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {

            if (newVal == null) {
                LOGGER.debug(LOGGER_CLASS_NAME, "setListener", "Selected value is null");
                return;
            }

            if (!uiFieldDTO.getId().equalsIgnoreCase(RegistrationConstants.RESIDENCE_STATUS)) {
                return;
            }

            String selectedCode = newVal.getCode();
            LOGGER.info(LOGGER_CLASS_NAME, "setListener",
                    "Residence status changed: " + selectedCode);

            FxControl nationalityControl =
                    GenericController.getFxControlMap().get(RegistrationConstants.NATIONALITY);

            if (nationalityControl == null) {
                LOGGER.error(LOGGER_CLASS_NAME, "setListener", "Nationality control not found");
                return;
            }

            ComboBox<GenericDto> nationalityCombo =
                    (ComboBox<GenericDto>) nationalityControl.getNode()
                            .lookup(RegistrationConstants.HASH + RegistrationConstants.NATIONALITY);

            if (nationalityCombo == null) {
                LOGGER.error(LOGGER_CLASS_NAME, "setListener", "Nationality ComboBox not found");
                return;
            }

            if (RegistrationConstants.ATTR_NON_FORINGER.equalsIgnoreCase(selectedCode)) {

                List<GenericDto> list = nationalityCombo.getItems();

                if (list == null || list.isEmpty()) {
                    LOGGER.warn(LOGGER_CLASS_NAME, "setListener", "Nationality list is empty");
                    return;
                }

                GenericDto selectedDto = null;
                for (GenericDto dto : list) {
                    if (RegistrationConstants.ATTR_SOUTH_AFRICAN_CODE
                            .equalsIgnoreCase(dto.getCode())) {
                        selectedDto = dto;
                        break;
                    }
                }

                if (selectedDto != null) {
                    nationalityCombo.getSelectionModel().select(selectedDto);
                    LOGGER.info(LOGGER_CLASS_NAME, "setListener",
                            "South Africa auto-selected");
                } else {
                    LOGGER.warn(LOGGER_CLASS_NAME, "setListener",
                            "South Africa code not found in list");
                }

                nationalityCombo.setMouseTransparent(true);

                nationalityControl.setData(null);

                LOGGER.debug(LOGGER_CLASS_NAME, "setListener",
                        "Nationality locked and DTO updated");

            } else {

                nationalityCombo.setMouseTransparent(false);
                nationalityCombo.getSelectionModel().clearSelection();
                nationalityCombo.setValue(null);

                getRegistrationDTo().removeDemographicField(RegistrationConstants.NATIONALITY);

                LOGGER.info(LOGGER_CLASS_NAME, "setListener",
                        "Nationality reset and enabled");
            }
        });
    }
}