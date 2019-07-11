package org.acme.travel.json;

import org.acme.travel.Traveller;

import io.quarkus.kafka.client.serialization.JsonbSerializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TravellerJsonbSerializer extends JsonbSerializer<Traveller> {


}
