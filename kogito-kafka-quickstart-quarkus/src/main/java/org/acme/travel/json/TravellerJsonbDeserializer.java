package org.acme.travel.json;

import org.acme.travel.Traveller;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TravellerJsonbDeserializer extends JsonbDeserializer<Traveller> {

	public TravellerJsonbDeserializer() {
		super(Traveller.class);
	}


}
