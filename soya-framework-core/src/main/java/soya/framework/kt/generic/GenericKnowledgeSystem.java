package soya.framework.kt.generic;

import soya.framework.kt.KnowledgeBuildException;
import soya.framework.kt.KnowledgeTree;
import soya.framework.kt.KnowledgeSystem;

import java.io.IOException;

public class GenericKnowledgeSystem<K, N> implements KnowledgeSystem<K, N> {

    private final Object source;
    private KnowledgeTree<K, N> knowledge;

    protected GenericKnowledgeSystem(Object source, KnowledgeTree<K, N> knowledge) {
        this.source = source;
        this.knowledge = knowledge;
    }

    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public KnowledgeTree<K, N> getKnowledge() {
        return knowledge;
    }

    public static Builder builder(Object source) {
        return new Builder(source);
    }

    public static class Builder<K, N> implements KnowledgeSystemBuilder<K, N> {
        private Object source;
        private KnowledgeExtractor<K> knowledgeExtractor;
        private KnowledgeDigester<K, N> digester;

        private Builder(Object source) {
            this.source = source;
        }

        @Override
        public Builder<K, N> knowledgeExtractor(KnowledgeExtractor<K> knowledgeExtractor) {
            this.knowledgeExtractor = knowledgeExtractor;
            return this;
        }

        @Override
        public KnowledgeSystemBuilder<K, N> knowledgeDigester(KnowledgeDigester<K, N> digester) {
            this.digester = digester;
            return this;
        }

        @Override
        public KnowledgeSystem<K, N> create() throws KnowledgeBuildException {
            try {
                return new GenericKnowledgeSystem<>(source, digester.digester(knowledgeExtractor.extract(source)));

            } catch (IOException e) {
                throw new KnowledgeBuildException(e);
            }
        }
    }
}
