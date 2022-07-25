package soya.framework.commons.knowledge;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KnowledgeSystems {
    private static Map<String, KnowledgeSystem<?, ?>> knowledgeSystemTypes = new ConcurrentHashMap<>();

}
