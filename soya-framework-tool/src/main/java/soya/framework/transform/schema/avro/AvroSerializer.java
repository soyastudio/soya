package soya.framework.transform.schema.avro;

import org.apache.avro.Schema;
import org.apache.avro.io.*;
import org.apache.avro.reflect.ReflectData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AvroSerializer<T> {

    private final Schema schema;
    private final ReflectData reflectData;

    public AvroSerializer(Schema schema) {
        this(schema, null);
    }

    public AvroSerializer(Schema schema, ReflectData reflectData) {
        this.schema = schema;
        if (reflectData == null) {
            this.reflectData = ReflectData.get();
        } else {
            this.reflectData = reflectData;
        }
    }

    // Writes obj to an output stream os
    public void writeToAvro(OutputStream os, T obj) throws IOException {
        DatumWriter<T> writer = reflectData.createDatumWriter(schema);
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(os, null);
        writer.write(obj, encoder);
        encoder.flush();
        os.flush();
    }

    // Reads an object from an Avro input stream
    public T readFromAvro(InputStream is) throws IOException {
        DatumReader<T> reader = reflectData.createDatumReader(schema);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(is, null);
        return reader.read(null, decoder);
    }

    public Schema getSchema() {
        return schema;
    }
}
