package soya.framework.dispatch.swagger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import soya.framework.dispatch.swagger.parameters.Parameter;
import soya.framework.dispatch.swagger.properties.Property;
import soya.framework.dispatch.swagger.refs.GenericRef;
import soya.framework.dispatch.swagger.refs.RefFormat;
import soya.framework.dispatch.swagger.refs.RefType;

/**
 * Created by Helmsdown on 7/8/15.
 *
 * This class extends directly from Path for now. At some future date we will need
 * to make {@link Path} an interface to follow the pattern established by
 * {@link Model}, {@link Property} and {@link Parameter}
 */
public class RefPath extends Path {

    private GenericRef genericRef;

    public RefPath() {
    }

    public RefPath(String ref) {
        set$ref(ref);
    }

    public RefPath(String ref, RefFormat refFormat) {
        this.genericRef = new GenericRef(RefType.PATH, ref, refFormat);
    }

    public void set$ref(String ref) {
        this.genericRef = new GenericRef(RefType.PATH, ref);
    }

    public String get$ref() {
        return genericRef.getRef();
    }

    /**
     * @since 1.5.21
     * @return originalRef
     */
    public String getOriginalRef() {
        if (genericRef != null) {
            return genericRef.getOriginalRef();
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RefPath refPath = (RefPath) o;

        return !(genericRef != null ? !genericRef.equals(refPath.genericRef) : refPath.genericRef != null);

    }

    @Override
    public int hashCode() {
        return genericRef != null ? genericRef.hashCode() : 0;
    }

    @JsonIgnore
    public RefFormat getRefFormat() {
        return this.genericRef.getFormat();
    }

}
