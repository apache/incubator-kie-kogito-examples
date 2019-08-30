package org.acme.travels.json;

import org.acme.travels.VisaApplication;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class VisaApplicationJsonbDeserializer extends JsonbDeserializer<VisaApplication> {

	public VisaApplicationJsonbDeserializer() {
		super(VisaApplication.class);
	}

}
