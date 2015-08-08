package outputModules.neo4j.importers;

import java.util.Map;

import neo4j.batchInserter.GraphNodeStore;
import neo4j.batchInserter.Neo4JBatchInserter;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.RelationshipType;

import outputModules.FunctionImporter;
import cfg.CFG;
import cfg.nodes.ASTNodeContainer;
import cfg.nodes.CFGNode;
import databaseNodes.DatabaseNode;
import databaseNodes.EdgeTypes;
import databaseNodes.FileDatabaseNode;
import databaseNodes.FunctionDatabaseNode;

// Stays alive while importing a function into
// the database

public class Neo4JFunctionImporter extends FunctionImporter
{
	protected GraphNodeStore nodeStore = new GraphNodeStore();

	public Neo4JFunctionImporter()
	{
		astImporter = new Neo4JASTImporter(nodeStore);
		cfgImporter = new Neo4JCFGImporter(nodeStore);
		udgImporter = new Neo4JUDGImporter(nodeStore);
		ddgImporter = new DDGImporter(nodeStore);
		cdgImporter = new CDGImporter(nodeStore);
	}

	// This is a copy of Neo4JaSTImporter.addMainNode
	@Override
	protected void addMainNode(DatabaseNode dbNode)
	{
		Map<String, Object> properties = dbNode.createProperties();
		nodeStore.addNeo4jNode(dbNode, properties);

		mainNodeId = nodeStore.getIdForObject(dbNode);

		properties.remove("location");
		nodeStore.indexNode(dbNode, properties);
	}

	@Override
	protected void linkFunctionWithAST(FunctionDatabaseNode function)
	{
		RelationshipType rel = DynamicRelationshipType
				.withName(EdgeTypes.IS_FUNCTION_OF_AST);

		long functionId = nodeStore.getIdForObject(function);
		long astNodeId = nodeStore.getIdForObject(function.getASTRoot());

		Neo4JBatchInserter.addRelationship(functionId, astNodeId, rel, null);

	}

	@Override
	protected void linkFunctionWithCFG(FunctionDatabaseNode function, CFG cfg)
	{
		RelationshipType rel = DynamicRelationshipType
				.withName(EdgeTypes.IS_FUNCTION_OF_CFG);
		long functionId = nodeStore.getIdForObject(function);

		CFGNode firstBlock = cfg.getEntryNode();

		long cfgRootId;
		try
		{
			cfgRootId = nodeStore.getIdForObject(firstBlock);
		}
		catch (RuntimeException ex)
		{
			cfgRootId = nodeStore
					.getIdForObject(((ASTNodeContainer) firstBlock)
							.getASTNode());
		}

		Neo4JBatchInserter.addRelationship(functionId, cfgRootId, rel, null);

	}

	@Override
	protected void linkFunctionToFileNode(FunctionDatabaseNode function,
			FileDatabaseNode fileNode)
	{
		RelationshipType rel = DynamicRelationshipType
				.withName(EdgeTypes.IS_FILE_OF);

		long fileId = fileNode.getId();
		long functionId = nodeStore.getIdForObject(function);

		Neo4JBatchInserter.addRelationship(fileId, functionId, rel, null);
	}

}