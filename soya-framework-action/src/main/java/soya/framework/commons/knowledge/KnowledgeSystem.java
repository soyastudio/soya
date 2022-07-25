package soya.framework.commons.knowledge;

import java.io.IOException;

public interface KnowledgeSystem<K, N> extends KnowledgeBase<KnowledgeTree<K, N>> {

    KnowledgeSystem<K, N> annotate(KnowledgeAnnotator annotator) throws KnowledgeProcessException;

    <T> T render(KnowledgeRenderer<T> renderer) throws KnowledgeProcessException;

    interface KnowledgeSystemBuilder<K, N> {

        KnowledgeSystemBuilder<K, N> knowledgeExtractor(KnowledgeExtractor<K> knowledgeExtractor);

        KnowledgeSystemBuilder<K, N> knowledgeDigester(KnowledgeDigester<K, N> digester);

        KnowledgeSystem<K, N> create() throws KnowledgeBuildException;
    }

    interface KnowledgeExtractor<K> {
        K extract(Object src) throws IOException, KnowledgeBuildException;
    }

    interface KnowledgeDigester<K, N> {
        KnowledgeTree<K, N> digester(K knowledge) throws KnowledgeBuildException;
    }

    interface KnowledgeAnnotator {
        void annotate(KnowledgeTree knowledgeTree) throws KnowledgeProcessException;
    }

    interface KnowledgeRenderer<T> {
        T render(KnowledgeTree knowledgeTree) throws KnowledgeProcessException;
    }
}
