/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wildfly.plugin.tools;

import java.io.IOException;
import java.util.Optional;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.controller.client.helpers.ClientConstants;
import org.jboss.as.controller.client.helpers.Operations;
import org.jboss.dmr.ModelNode;

/**
 * A default implementation for the {@link ContainerDescription}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class DefaultContainerDescription implements ContainerDescription {

    private final String productName;
    private final String productVersion;
    private final String releaseVersion;
    private final String launchType;
    private final boolean isDomain;
    private final ModelVersion modelVersion;

    private DefaultContainerDescription(final String productName, final String productVersion,
                                        final String releaseVersion, final ModelVersion modelVersion, final String launchType, final boolean isDomain) {
        this.productName = productName;
        this.productVersion = productVersion;
        this.releaseVersion = releaseVersion;
        this.launchType = launchType;
        this.isDomain = isDomain;
        this.modelVersion = modelVersion;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public String getProductVersion() {
        return productVersion;
    }

    @Override
    public String getReleaseVersion() {
        return releaseVersion;
    }

    @Override
    public ModelVersion getModelVersion() {
        return modelVersion;
    }

    @Override
    public String getLaunchType() {
        return launchType;
    }

    @Override
    public boolean isDomain() {
        return isDomain;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(64);
        result.append(productName);
        if (productVersion != null) {
            result.append(' ').append(productVersion);
            if (releaseVersion != null) {
                result.append(" (WildFly Core ").append(releaseVersion).append(')');
            }
        } else {
            if (releaseVersion != null) {
                result.append(' ').append(releaseVersion);
            }
        }
        if (launchType != null) {
            result.append(" - launch-type: ").append(launchType);
        }
        return result.toString();
    }

    /**
     * Queries the running container and attempts to lookup the information from the running container.
     *
     * @param client the client used to execute the management operation
     *
     * @return the container description
     *
     * @throws IOException                 if an error occurs while executing the management operation
     * @throws OperationExecutionException if the operation used to query the container fails
     */
    static DefaultContainerDescription lookup(final ModelControllerClient client)
            throws IOException, OperationExecutionException {
        final ModelNode op = Operations.createReadResourceOperation(new ModelNode().setEmptyList());
        op.get(ClientConstants.INCLUDE_RUNTIME).set(true);
        final ModelNode result = client.execute(op);
        if (Operations.isSuccessfulOutcome(result)) {
            final ModelNode model = Operations.readResult(result);
            final String productName = getValue(model, "product-name", "WildFly");
            final String productVersion = getValue(model, "product-version");
            final String releaseVersion = getValue(model, "release-version");
            final String launchType = getValue(model, "launch-type");
            final ModelVersion modelVersion = new ModelVersion(
                    model.get("management-major-version").asInt(0),
                    model.get("management-minor-version").asInt(0),
                    model.get("management-micro-version").asInt(0));
            return new DefaultContainerDescription(productName, productVersion, releaseVersion, modelVersion,
                    launchType, "DOMAIN".equalsIgnoreCase(launchType));
        }
        throw new OperationExecutionException(op, result);
    }

    private static String getValue(final ModelNode model, final String attributeName) {
        return getValue(model, attributeName, null);
    }

    private static String getValue(final ModelNode model, final String attributeName, final String defaultValue) {
        if (model.hasDefined(attributeName)) {
            return model.get(attributeName).asString();
        }
        return defaultValue;
    }
}
