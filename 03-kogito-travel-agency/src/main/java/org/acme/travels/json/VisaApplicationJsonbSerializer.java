package org.acme.travels.json;

import org.acme.travels.VisaApplication;

import io.quarkus.kafka.client.serialization.JsonbSerializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class VisaApplicationJsonbSerializer extends JsonbSerializer<VisaApplication> {

}
